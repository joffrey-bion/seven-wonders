package org.luxons.sevenwonders.bot

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.luxons.sevenwonders.client.*
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.actions.BotConfig
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import org.slf4j.LoggerFactory
import kotlin.random.Random

private val logger = LoggerFactory.getLogger(SevenWondersBot::class.java)

suspend fun SevenWondersClient.connectBot(
    serverUrl: String,
    name: String,
    config: BotConfig = BotConfig(),
): SevenWondersBot {
    logger.info("Connecting new bot '$name' to $serverUrl")
    val session = connect(serverUrl)
    val player = session.chooseNameAndAwait(name, Icon("desktop"), isHuman = false)
    return SevenWondersBot(player, config, session)
}

suspend fun SevenWondersClient.connectBots(
    serverUrl: String,
    names: List<String>,
    config: BotConfig = BotConfig(),
): List<SevenWondersBot> = names.map { connectBot(serverUrl, it, config) }

class SevenWondersBot(
    private val player: ConnectedPlayer,
    private val config: BotConfig = BotConfig(),
    private val session: SevenWondersSession,
) {
    suspend fun disconnect() {
        session.disconnect()
    }

    suspend fun createGameWithBotFriendsAndAutoPlay(
        gameName: String,
        otherBots: List<SevenWondersBot>,
        customWonders: List<AssignedWonder>? = null,
        customSettings: Settings? = null,
    ): PlayerTurnInfo<*> {
        val nJoinerBots = otherBots.size
        require(nJoinerBots >= 2) { "At least 2 more bots must join the game" }
        require(customWonders == null || customWonders.size == nJoinerBots + 1) {
            "Custom wonders don't match the number of players in the game"
        }

        val lobby = session.createGameAndAwaitLobby(gameName)
        otherBots.forEach {
            it.session.joinGameAndAwaitLobby(lobby.id)
        }

        customWonders?.let { session.reassignWonders(it) }
        customSettings?.let { session.updateSettings(it) }

        return withContext(Dispatchers.Default) {
            otherBots.forEach {
                launch { it.autoPlayUntilEnd() }
            }
            val endTurn = async { autoPlayUntilEnd() }
            session.startGame()
            endTurn.await()
        }
    }

    suspend fun joinAndAutoPlay(gameId: Long) = coroutineScope {
        launch { autoPlayUntilEnd() }
        session.joinGame(gameId)
    }

    private suspend fun autoPlayUntilEnd(): PlayerTurnInfo<TurnAction.WatchScore> = coroutineScope {
        val endGameTurnInfo = async {
            @Suppress("UNCHECKED_CAST")
            session.watchTurns()
                .filter { it.action is TurnAction.WatchScore }
                .first() as PlayerTurnInfo<TurnAction.WatchScore>
        }
        session.watchGameEvents()
            .catch { e -> logger.error("BOT $player: error in game events flow", e) }
            .takeWhile { it !is GameEvent.LobbyLeft }
            .collect { event ->
                when (event) {
                    is GameEvent.NameChosen -> error("Unexpected name chosen event in bot")
                    is GameEvent.GameStarted -> session.sayReady()
                    is GameEvent.NewTurnStarted -> if (event.turnInfo.action is TurnAction.WatchScore) {
                        logger.info("BOT $player: leaving the game")
                        session.leaveGame()
                    } else {
                        botDelay()
                        logger.info("BOT $player: playing turn (action ${event.turnInfo.action})")
                        session.autoPlayTurn(event.turnInfo)
                    }
                    is GameEvent.LobbyJoined,
                    is GameEvent.LobbyUpdated,
                    is GameEvent.PlayerIsReady,
                    is GameEvent.MovePrepared,
                    GameEvent.MoveUnprepared,
                    is GameEvent.CardPrepared -> Unit // ignore those
                    GameEvent.LobbyLeft -> error("Unexpected lobby left event in bot") // collect should have ended
                }
            }
        val lastTurn = endGameTurnInfo.await()
        session.disconnect()
        logger.info("BOT $player: disconnected")
        lastTurn
    }

    private suspend fun botDelay() {
        val timeMillis = if (config.minActionDelayMillis == config.maxActionDelayMillis) {
            config.minActionDelayMillis
        } else {
            Random.nextLong(config.minActionDelayMillis, config.maxActionDelayMillis)
        }
        delay(timeMillis)
    }
}

@Suppress("UNCHECKED_CAST")
private suspend fun SevenWondersSession.autoPlayTurn(turn: PlayerTurnInfo<*>) {
    when (val action = turn.action) {
        is TurnAction.PlayFromHand -> prepareMove(createPlayCardMove(turn as PlayerTurnInfo<TurnAction.PlayFromHand>))
        is TurnAction.PlayFromDiscarded -> prepareMove(createPlayFreeDiscardedCardMove(action))
        is TurnAction.PickNeighbourGuild -> prepareMove(createPickGuildMove(action))
        is TurnAction.SayReady -> sayReady()
        is TurnAction.Wait,
        is TurnAction.WatchScore -> Unit
    }
}

private fun createPlayCardMove(turnInfo: PlayerTurnInfo<TurnAction.PlayFromHand>): PlayerMove {
    val wonderBuildability = turnInfo.wonderBuildability
    val hand = turnInfo.action.hand
    if (wonderBuildability.isBuildable) {
        val transactions = wonderBuildability.transactionsOptions.random()
        return PlayerMove(MoveType.UPGRADE_WONDER, hand.random().name, transactions)
    }
    val playableCard = hand.filter { it.playability.isPlayable }.randomOrNull()
    return if (playableCard != null) {
        PlayerMove(MoveType.PLAY, playableCard.name, playableCard.playability.transactionOptions.random())
    } else {
        PlayerMove(MoveType.DISCARD, hand.random().name, noTransactions())
    }
}

private fun createPlayFreeDiscardedCardMove(action: TurnAction.PlayFromDiscarded): PlayerMove {
    val card = action.discardedCards.random()
    return PlayerMove(MoveType.PLAY_FREE_DISCARDED, card.name)
}

private fun createPickGuildMove(action: TurnAction.PickNeighbourGuild): PlayerMove =
    PlayerMove(MoveType.COPY_GUILD, action.neighbourGuildCards.random().name)

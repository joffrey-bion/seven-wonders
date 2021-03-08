package org.luxons.sevenwonders.bot

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.luxons.sevenwonders.client.*
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.actions.BotConfig
import org.luxons.sevenwonders.model.api.actions.Icon
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
    val player = session.chooseName(name, Icon("desktop"), isHuman = false)
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
                launch {
                    val turn = it.session.watchGameStarted().first()
                    it.autoPlayUntilEnd(turn)
                }
            }
            val firstTurn = session.startGameAndAwaitFirstTurn()
            autoPlayUntilEnd(firstTurn)
        }
    }

    suspend fun joinAndAutoPlay(gameId: Long): PlayerTurnInfo<*> {
        val firstTurn = session.joinGameAndAwaitFirstTurn(gameId)
        return autoPlayUntilEnd(firstTurn)
    }

    private suspend fun autoPlayUntilEnd(currentTurn: PlayerTurnInfo<*>) = coroutineScope {
        val endGameTurnInfo = async {
            session.watchTurns().filter { it.action is TurnAction.WatchScore }.first()
        }
        session.watchTurns()
            .onStart {
                session.sayReady()
                emit(currentTurn)
            }
            .takeWhile { it.action !is TurnAction.WatchScore }
            .catch { e -> logger.error("BOT $player: error in turnInfo flow", e) }
            .collect { turn ->
                botDelay()
                logger.info("BOT $player: playing turn (action ${turn.action})")
                session.autoPlayTurn(turn)
            }
        val lastTurn = endGameTurnInfo.await()
        logger.info("BOT $player: leaving the game")
        session.leaveGameAndAwaitEnd()
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

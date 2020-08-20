package org.luxons.sevenwonders.bot

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.withTimeout
import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.model.Action
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.model.resources.noTransactions
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.hours

@OptIn(ExperimentalTime::class)
data class BotConfig(
    val minActionDelayMillis: Long = 500,
    val maxActionDelayMillis: Long = 1000,
    val globalTimeout: Duration = 10.hours
)

@OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class)
class SevenWondersBot(
    private val displayName: String,
    private val botConfig: BotConfig = BotConfig()
) {
    private val client = SevenWondersClient()

    suspend fun play(serverUrl: String, gameId: Long) = withTimeout(botConfig.globalTimeout) {
        val session = client.connect(serverUrl)
        session.chooseName(displayName, Icon("desktop"))
        session.joinGame(gameId)
        val firstTurn = session.awaitGameStart(gameId)

        session.watchTurns()
            .onStart { emit(firstTurn) }
            .takeWhile { it.action != Action.WATCH_SCORE }
            .onCompletion { session.disconnect() }
            .collect { turn ->
                botDelay()
                session.playTurn(turn)
            }
    }

    private suspend fun botDelay() {
        delay(Random.nextLong(botConfig.minActionDelayMillis, botConfig.maxActionDelayMillis))
    }

    private suspend fun SevenWondersSession.playTurn(turn: PlayerTurnInfo) {
        when (turn.action) {
            Action.PLAY, Action.PLAY_2, Action.PLAY_LAST -> prepareMove(createPlayCardMove(turn))
            Action.PLAY_FREE_DISCARDED -> prepareMove(createPlayFreeDiscardedCardMove(turn))
            Action.PICK_NEIGHBOR_GUILD -> prepareMove(createPickGuildMove(turn))
            Action.SAY_READY -> sayReady()
            Action.WAIT, Action.WATCH_SCORE -> Unit
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun createPlayCardMove(turnInfo: PlayerTurnInfo): PlayerMove {
    val hand = turnInfo.hand ?: error("Cannot choose move, no hand in current turn info!")
    val wonderBuildability = turnInfo.wonderBuildability
    if (wonderBuildability.isBuildable) {
        val transactions = wonderBuildability.cheapestTransactions.random()
        return PlayerMove(MoveType.UPGRADE_WONDER, hand.random().name, transactions)
    }
    val playableCard = hand.filter { it.playability.isPlayable }.randomOrNull()
    return if (playableCard != null) {
        PlayerMove(MoveType.PLAY, playableCard.name, playableCard.playability.cheapestTransactions.random())
    } else {
        PlayerMove(MoveType.DISCARD, hand.random().name, noTransactions())
    }
}

private fun createPlayFreeDiscardedCardMove(turnInfo: PlayerTurnInfo): PlayerMove {
    val card = turnInfo.discardedCards?.random() ?: error("No discarded card to play")
    return PlayerMove(MoveType.PLAY_FREE_DISCARDED, card.name)
}

private fun createPickGuildMove(turnInfo: PlayerTurnInfo): PlayerMove =
    PlayerMove(MoveType.COPY_GUILD, turnInfo.neighbourGuildCards.random().name)

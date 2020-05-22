package org.luxons.sevenwonders.bot

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalTime::class)
class SevenWondersBot(
    private val displayName: String,
    private val botConfig: BotConfig = BotConfig()
) {
    private val client = SevenWondersClient()

    suspend fun play(serverHost: String, gameId: Long) = withTimeout(botConfig.globalTimeout) {
        val session = client.connect(serverHost)
        session.chooseName(displayName, Icon("desktop"))
        session.joinGame(gameId)
        session.awaitGameStart(gameId)

        coroutineScope {
            launch {
                session.watchTurns().first { turn ->
                    botDelay()
                    val keepPlaying = session.playTurn(turn)
                    !keepPlaying
                }
            }
            botDelay()
            session.sayReady() // triggers the first turn
        }
        session.disconnect()
    }

    private suspend fun botDelay() {
        delay(Random.nextLong(botConfig.minActionDelayMillis, botConfig.maxActionDelayMillis))
    }

    private suspend fun SevenWondersSession.playTurn(turn: PlayerTurnInfo): Boolean {
        when (turn.action) {
            Action.PLAY, Action.PLAY_2, Action.PLAY_LAST -> prepareMove(createPlayCardMove(turn))
            Action.PICK_NEIGHBOR_GUILD -> prepareMove(createPickGuildMove(turn))
            Action.SAY_READY -> sayReady()
            Action.WAIT -> Unit
            Action.WATCH_SCORE -> return false
        }
        return true
    }
}

private fun createPlayCardMove(turnInfo: PlayerTurnInfo): PlayerMove {
    val wonderBuildability = turnInfo.wonderBuildability
    if (wonderBuildability.isBuildable) {
        val transactions = wonderBuildability.cheapestTransactions.first()
        return PlayerMove(MoveType.UPGRADE_WONDER, turnInfo.hand!!.first().name, transactions)
    }
    val playableCard = turnInfo.hand!!.firstOrNull { it.playability.isPlayable }
    return if (playableCard != null) {
        PlayerMove(MoveType.PLAY, playableCard.name, playableCard.playability.cheapestTransactions.first())
    } else {
        PlayerMove(MoveType.DISCARD, turnInfo.hand!!.first().name, noTransactions())
    }
}

private fun createPickGuildMove(turnInfo: PlayerTurnInfo): PlayerMove =
    PlayerMove(MoveType.COPY_GUILD, turnInfo.neighbourGuildCards.first().name)

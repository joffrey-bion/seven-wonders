package org.luxons.sevenwonders.server.controllers

import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.withTimeout
import org.luxons.sevenwonders.bot.connectBot
import org.luxons.sevenwonders.bot.connectBots
import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.model.TurnAction
import org.luxons.sevenwonders.model.api.AutoGameAction
import org.luxons.sevenwonders.model.api.AutoGameResult
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import kotlin.time.Duration.Companion.minutes
import kotlin.time.measureTimedValue
import kotlin.time.toJavaDuration

/**
 * Handles actions in the game's lobby. The lobby is the place where players gather before a game.
 */
@RestController
class AutoGameController(
    @param:Value("\${server.port}") private val serverPort: String,
    private val meterRegistry: MeterRegistry,
) {
    @PostMapping("/autoGame")
    suspend fun autoGame(@RequestBody action: AutoGameAction, principal: Principal): AutoGameResult {
        logger.info("Starting auto-game {}", action.gameName)
        val client = SevenWondersClient()
        val serverUrl = "ws://localhost:$serverPort"

        val lastTurn = withTimeout(5.minutes) {
            val (lastTurn, duration) = measureTimedValue {
                val otherBotNames = List(action.nbPlayers - 1) { "JoinerBot${it + 1}" }
                val owner = client.connectBot(serverUrl, "OwnerBot", action.config)
                val joiners = client.connectBots(serverUrl, otherBotNames, action.config)

                owner.createGameWithBotFriendsAndAutoPlay(
                    gameName = action.gameName,
                    otherBots = joiners,
                    customWonders = action.customWonders,
                    customSettings = action.customSettings,
                )
            }
            meterRegistry.timer("autogame.duration").record(duration.toJavaDuration())
            lastTurn
        }

        val turnAction = lastTurn.action as? TurnAction.WatchScore ?: error("Last turn action should be to watch the score")
        val scoreBoard = turnAction.scoreBoard
        return AutoGameResult(scoreBoard, lastTurn.table)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AutoGameController::class.java)
    }
}

package org.luxons.sevenwonders.server.controllers

import io.micrometer.core.instrument.MeterRegistry
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.actions.ChooseNameAction
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import java.security.Principal

/**
 * Handles actions in the homepage of the game.
 */
@Controller
class HomeController(
    private val messenger: SimpMessageSendingOperations,
    private val playerRepository: PlayerRepository,
    private val meterRegistry: MeterRegistry,
) {
    /**
     * Creates/updates the player's name (for the user's session).
     *
     * @param action the action to choose the name of the player
     * @param principal the connected user's information
     *
     * @return the created [PlayerDTO] object
     */
    @MessageMapping("/chooseName")
    fun chooseName(@Validated action: ChooseNameAction, principal: Principal) {
        val username = principal.name
        val player = playerRepository.createOrUpdate(username, action.playerName, action.isHuman, action.icon)

        meterRegistry.counter("players.connections").increment()
        logger.info("Player '{}' chose the name '{}'", username, player.displayName)
        val connectedPlayer = ConnectedPlayer(username, player.displayName, player.isHuman, player.icon)
        messenger.sendGameEvent(player, GameEvent.NameChosen(connectedPlayer))
    }

    companion object {
        private val logger = LoggerFactory.getLogger(HomeController::class.java)
    }
}

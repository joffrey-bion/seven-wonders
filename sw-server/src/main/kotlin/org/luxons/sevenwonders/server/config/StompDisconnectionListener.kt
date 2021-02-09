package org.luxons.sevenwonders.server.config

import org.luxons.sevenwonders.server.controllers.GameController
import org.luxons.sevenwonders.server.controllers.LobbyController
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
internal class StompDisconnectionListener(
    private val lobbyController: LobbyController,
    private val playerRepository: PlayerRepository,
    private val gameController: GameController,
) : ApplicationListener<SessionDisconnectEvent> {

    private val logger: Logger = LoggerFactory.getLogger(StompDisconnectionListener::class.java)

    override fun onApplicationEvent(event: SessionDisconnectEvent) {
        val principal = event.user
        if (principal == null) {
            logger.error("Received session disconnect event without a principal (sessionId = ${event.sessionId})")
            return
        }

        val player = playerRepository.find(principal.name)
        if (player == null) {
            logger.error("Received session disconnect event from unknown player '${principal.name}'")
            return
        }

        logger.info("User $player disconnected (sessionId = ${event.sessionId})")

        when {
            // TODO auto-play until the end? https://github.com/joffrey-bion/seven-wonders/issues/85
            player.isInGame -> gameController.leave(principal)
            player.isInLobby -> lobbyController.leave(principal)
            else -> playerRepository.remove(principal.name)
        }
    }
}

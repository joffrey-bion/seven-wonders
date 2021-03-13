package org.luxons.sevenwonders.server.config

import io.micrometer.core.instrument.MeterRegistry
import org.luxons.sevenwonders.server.controllers.GameController
import org.luxons.sevenwonders.server.controllers.LobbyController
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.AbstractSubProtocolEvent
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent
import java.util.concurrent.atomic.AtomicInteger

@Component
internal class StompDisconnectionListener(
    private val lobbyController: LobbyController,
    private val playerRepository: PlayerRepository,
    private val gameController: GameController,
    meterRegistry: MeterRegistry,
) : ApplicationListener<AbstractSubProtocolEvent> {

    private val logger = LoggerFactory.getLogger(StompDisconnectionListener::class.java)

    private val activeConnections: AtomicInteger = meterRegistry.gauge("clients.connected", AtomicInteger(0))!!

    override fun onApplicationEvent(event: AbstractSubProtocolEvent) {
        when (event) {
            is SessionConnectedEvent -> onConnected()
            is SessionDisconnectEvent -> onDisconnect(event)
        }
    }

    private fun onConnected() {
        activeConnections.incrementAndGet()
    }

    private fun onDisconnect(event: SessionDisconnectEvent) {
        activeConnections.decrementAndGet()
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
        }
        playerRepository.remove(principal.name)
    }
}

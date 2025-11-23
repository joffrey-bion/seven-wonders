package org.luxons.sevenwonders.server.controllers

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.luxons.sevenwonders.model.api.actions.ChooseNameAction
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.luxons.sevenwonders.server.test.MockMessageChannel
import org.luxons.sevenwonders.server.test.expectSentGameEventTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import kotlin.test.Test
import kotlin.test.assertEquals

class HomeControllerTest {

    @Test
    fun chooseName() {
        val meterRegistry = SimpleMeterRegistry()
        val playerRepository = PlayerRepository(meterRegistry)
        val messageChannel = MockMessageChannel()
        val messenger = SimpMessagingTemplate(messageChannel)
        val homeController = HomeController(messenger, playerRepository, meterRegistry)

        val action = ChooseNameAction("Test User", Icon("person"), isHuman = true)
        val principal = TestPrincipal("testuser")

        homeController.chooseName(action, principal)

        val payload = messageChannel.expectSentGameEventTo<GameEvent.NameChosen>("testuser")
        val player = payload.player
        assertEquals("testuser", player.username)
        assertEquals("Test User", player.displayName)
        assertEquals("person", player.icon?.name)

        messageChannel.expectNoMoreMessages()
    }
}

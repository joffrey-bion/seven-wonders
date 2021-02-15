package org.luxons.sevenwonders.server.controllers

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.Test
import org.luxons.sevenwonders.model.api.actions.ChooseNameAction
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import kotlin.test.assertEquals

class HomeControllerTest {

    @Test
    fun chooseName() {
        val meterRegistry = SimpleMeterRegistry()
        val playerRepository = PlayerRepository(meterRegistry)
        val homeController = HomeController(playerRepository, meterRegistry)

        val action = ChooseNameAction("Test User", Icon("person"), isHuman = true)
        val principal = TestPrincipal("testuser")

        val player = homeController.chooseName(action, principal)

        assertEquals("testuser", player.username)
        assertEquals("Test User", player.displayName)
        assertEquals("person", player.icon?.name)
    }
}

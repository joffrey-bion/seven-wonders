package org.luxons.sevenwonders.controllers

import org.junit.Test
import org.luxons.sevenwonders.actions.ChooseNameAction
import org.luxons.sevenwonders.repositories.PlayerRepository
import kotlin.test.assertEquals

class HomeControllerTest {

    @Test
    fun chooseName() {
        val playerRepository = PlayerRepository()
        val homeController = HomeController(playerRepository)

        val action = ChooseNameAction("Test User")
        val principal = TestPrincipal("testuser")

        val player = homeController.chooseName(action, principal)

        assertEquals("testuser", player.username)
        assertEquals("Test User", player.displayName)
        assertEquals(false, player.isGameOwner)
        assertEquals(true, player.isUser)
    }
}

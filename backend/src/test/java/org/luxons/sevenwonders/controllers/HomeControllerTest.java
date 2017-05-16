package org.luxons.sevenwonders.controllers;

import java.security.Principal;

import org.junit.Test;
import org.luxons.sevenwonders.actions.ChooseNameAction;
import org.luxons.sevenwonders.test.TestUtils;
import org.luxons.sevenwonders.lobby.Player;
import org.luxons.sevenwonders.repositories.PlayerRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class HomeControllerTest {

    @Test
    public void chooseName() {
        PlayerRepository playerRepository = new PlayerRepository();
        HomeController homeController = new HomeController(playerRepository);

        ChooseNameAction action = new ChooseNameAction();
        action.setPlayerName("Test User");

        Principal principal = TestUtils.createPrincipal("testuser");

        Player player = homeController.chooseName(action, principal);

        assertSame(player, playerRepository.find("testuser"));
        assertEquals("testuser", player.getUsername());
        assertEquals("Test User", player.getDisplayName());
        assertEquals(null, player.getLobby());
        assertEquals(null, player.getGame());
    }
}

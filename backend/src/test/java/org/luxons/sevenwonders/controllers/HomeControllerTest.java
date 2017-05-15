package org.luxons.sevenwonders.controllers;

import java.security.Principal;

import org.junit.Test;
import org.luxons.sevenwonders.actions.ChooseNameAction;
import org.luxons.sevenwonders.controllers.test.TestUtils;
import org.luxons.sevenwonders.repositories.PlayerRepository;

import static org.junit.Assert.assertTrue;

public class HomeControllerTest {

    @Test
    public void chooseName() {
        PlayerRepository playerRepository = new PlayerRepository();
        HomeController homeController = new HomeController(playerRepository);

        ChooseNameAction action = new ChooseNameAction();
        action.setPlayerName("Test User");

        Principal principal = TestUtils.createPrincipal("testuser");

        homeController.chooseName(action, principal);

        assertTrue(playerRepository.contains("testuser"));
    }
}

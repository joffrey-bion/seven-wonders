package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.actions.CreateGameAction;
import org.luxons.sevenwonders.actions.JoinGameAction;
import org.luxons.sevenwonders.controllers.GameBrowserController.UserAlreadyInGameException;
import org.luxons.sevenwonders.test.TestUtils;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.luxons.sevenwonders.lobby.Lobby;
import org.luxons.sevenwonders.lobby.Player;
import org.luxons.sevenwonders.repositories.LobbyRepository;
import org.luxons.sevenwonders.repositories.PlayerRepository;
import org.luxons.sevenwonders.repositories.PlayerRepository.PlayerNotFoundException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class GameBrowserControllerTest {

    private PlayerRepository playerRepository;

    private GameBrowserController gameBrowserController;

    @Before
    public void setUp() {
        playerRepository = new PlayerRepository();
        LobbyRepository lobbyRepository = new LobbyRepository(new GameDefinitionLoader());
        SimpMessagingTemplate template = TestUtils.createSimpMessagingTemplate();
        LobbyController lobbyController = new LobbyController(playerRepository, template);
        gameBrowserController = new GameBrowserController(lobbyController, lobbyRepository, playerRepository, template);
    }

    @Test
    public void listGames_initiallyEmpty() {
        Principal principal = new TestPrincipal("testuser");
        Collection<Lobby> games = gameBrowserController.listGames(principal);
        assertTrue(games.isEmpty());
    }

    @Test
    public void createGame_success() {
        Player player = playerRepository.createOrUpdate("testuser", "Test User");
        Principal principal = new TestPrincipal("testuser");

        CreateGameAction action = new CreateGameAction();
        action.setGameName("Test Game");

        Lobby createdLobby = gameBrowserController.createGame(action, principal);

        assertEquals("Test Game", createdLobby.getName());

        Collection<Lobby> games = gameBrowserController.listGames(principal);
        assertFalse(games.isEmpty());
        Lobby lobby = games.iterator().next();
        assertSame(lobby, createdLobby);
        assertSame(player, lobby.getPlayers().get(0));
    }

    @Test(expected = PlayerNotFoundException.class)
    public void createGame_failsForUnknownPlayer() {
        Principal principal = new TestPrincipal("unknown");

        CreateGameAction action = new CreateGameAction();
        action.setGameName("Test Game");

        gameBrowserController.createGame(action, principal);
    }

    @Test(expected = UserAlreadyInGameException.class)
    public void createGame_failsWhenAlreadyInGame() {
        playerRepository.createOrUpdate("testuser", "Test User");
        Principal principal = new TestPrincipal("testuser");

        CreateGameAction createGameAction1 = new CreateGameAction();
        createGameAction1.setGameName("Test Game 1");

        // auto-enters the game
        gameBrowserController.createGame(createGameAction1, principal);

        CreateGameAction createGameAction2 = new CreateGameAction();
        createGameAction2.setGameName("Test Game 2");

        // already in a game
        gameBrowserController.createGame(createGameAction2, principal);
    }

    @Test
    public void joinGame_success() {
        Player owner = playerRepository.createOrUpdate("testowner", "Test User Owner");
        Principal ownerPrincipal = new TestPrincipal("testowner");
        CreateGameAction createGameAction = new CreateGameAction();
        createGameAction.setGameName("Test Game");

        Lobby createdLobby = gameBrowserController.createGame(createGameAction, ownerPrincipal);

        Player joiner = playerRepository.createOrUpdate("testjoiner", "Test User Joiner");
        Principal joinerPrincipal = new TestPrincipal("testjoiner");
        JoinGameAction joinGameAction = new JoinGameAction();
        joinGameAction.setGameId(createdLobby.getId());

        Lobby joinedLobby = gameBrowserController.joinGame(joinGameAction, joinerPrincipal);

        assertSame(createdLobby, joinedLobby);
        assertSame(owner, joinedLobby.getPlayers().get(0));
        assertSame(joiner, joinedLobby.getPlayers().get(1));
    }

    @Test(expected = UserAlreadyInGameException.class)
    public void joinGame_failsWhenAlreadyInGame() {
        playerRepository.createOrUpdate("testowner", "Test User Owner");
        Principal ownerPrincipal = new TestPrincipal("testowner");
        CreateGameAction createGameAction = new CreateGameAction();
        createGameAction.setGameName("Test Game");

        Lobby createdLobby = gameBrowserController.createGame(createGameAction, ownerPrincipal);

        playerRepository.createOrUpdate("testjoiner", "Test User Joiner");
        Principal joinerPrincipal = new TestPrincipal("testjoiner");
        JoinGameAction joinGameAction = new JoinGameAction();
        joinGameAction.setGameId(createdLobby.getId());

        // joins the game
        gameBrowserController.joinGame(joinGameAction, joinerPrincipal);
        // already in a game
        gameBrowserController.joinGame(joinGameAction, joinerPrincipal);
    }
}

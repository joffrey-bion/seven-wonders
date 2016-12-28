package org.luxons.sevenwonders.game;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.Lobby.GameAlreadyStartedException;
import org.luxons.sevenwonders.game.Lobby.PlayerNameAlreadyUsedException;
import org.luxons.sevenwonders.game.Lobby.PlayerOverflowException;
import org.luxons.sevenwonders.game.Lobby.PlayerUnderflowException;
import org.luxons.sevenwonders.game.Lobby.UnknownPlayerException;
import org.luxons.sevenwonders.game.data.GameDefinition;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

@RunWith(Theories.class)
public class LobbyTest {

    @DataPoints
    public static int[] nbPlayers() {
        return new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static GameDefinition gameDefinition;

    private Player gameOwner;

    private Lobby lobby;

    @BeforeClass
    public static void loadDefinition() {
        gameDefinition = new GameDefinitionLoader().getGameDefinition();
    }

    @Before
    public void setUp() {
        gameOwner = new Player("Game owner", "gameowner");
        lobby = new Lobby(0, "Test Game", gameOwner, gameDefinition);
    }

    @Test
    public void isOwner_falseWhenNull() {
        assertFalse(lobby.isOwner(null));
    }

    @Test
    public void isOwner_falseWhenEmptyString() {
        assertFalse(lobby.isOwner(""));
    }

    @Test
    public void isOwner_falseWhenGarbageString() {
        assertFalse(lobby.isOwner("this is garbage"));
    }

    @Test
    public void isOwner_trueWhenOwnerUserName() {
        assertTrue(lobby.isOwner(gameOwner.getUserName()));
    }

    @Test
    public void isOwner_falseWhenOtherPlayerName() {
        Player player = new Player("Test User", "testuser");
        lobby.addPlayer(player);
        assertFalse(lobby.isOwner(player.getUserName()));
    }

    @Test
    public void addPlayer_success() {
        Player player = new Player("Test User", "testuser");
        lobby.addPlayer(player);
        assertTrue(lobby.containsUser("testuser"));
    }

    @Test(expected = PlayerNameAlreadyUsedException.class)
    public void addPlayer_failsOnSameName() {
        Player player = new Player("Test User", "testuser");
        Player player2 = new Player("Test User", "testuser2");
        lobby.addPlayer(player);
        lobby.addPlayer(player2);
    }

    @Test(expected = PlayerOverflowException.class)
    public void addPlayer_playerOverflowWhenTooMany() {
        // the owner + the max number gives an overflow
        addPlayers(gameDefinition.getMaxPlayers());
    }

    @Test(expected = GameAlreadyStartedException.class)
    public void addPlayer_failWhenGameStarted() {
        // total with owner is the minimum
        addPlayers(gameDefinition.getMinPlayers() - 1);
        lobby.startGame(new Settings());
        lobby.addPlayer(new Player("The Late Guy", "soonerNextTime"));
    }

    private void addPlayers(int nbPlayers) {
        for (int i = 0; i < nbPlayers; i++) {
            Player player = new Player("Test User " + i, "testuser" + i);
            lobby.addPlayer(player);
        }
    }

    @Test
    public void reorderPlayers_failsOnSameName() {
        Player player1 = new Player("Test User 1", "testuser1");
        Player player2 = new Player("Test User 2", "testuser2");
        Player player3 = new Player("Test User 3", "testuser3");
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);
        lobby.addPlayer(player3);
        lobby.reorderPlayers(Arrays.asList("testuser3", "testuser1", "testuser2"));
        assertEquals("testuser3", lobby.getPlayers().get(0).getUserName());
        assertEquals("testuser1", lobby.getPlayers().get(1).getUserName());
        assertEquals("testuser2", lobby.getPlayers().get(2).getUserName());
    }

    @Test(expected = UnknownPlayerException.class)
    public void reorderPlayers_failsOnUnknownPlayer() {
        Player player1 = new Player("Test User 1", "testuser1");
        Player player2 = new Player("Test User 2", "testuser2");
        Player player3 = new Player("Test User 3", "testuser3");
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);
        lobby.addPlayer(player3);
        lobby.reorderPlayers(Arrays.asList("testuser4", "testuser1", "testuser2"));
    }

    @Theory
    public void startGame_failsBelowMinPlayers(int nbPlayers) {
        assumeTrue(nbPlayers < gameDefinition.getMinPlayers());
        thrown.expect(PlayerUnderflowException.class);
        // there is already the owner
        addPlayers(nbPlayers - 1);
        lobby.startGame(new Settings());
    }

    @Theory
    public void startGame_succeedsAboveMinPlayers(int nbPlayers) {
        assumeTrue(nbPlayers >= gameDefinition.getMinPlayers());
        assumeTrue(nbPlayers < gameDefinition.getMaxPlayers());
        // there is already the owner
        addPlayers(nbPlayers - 1);
        lobby.startGame(new Settings());
    }
}
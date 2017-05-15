package org.luxons.sevenwonders.lobby;

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
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.data.GameDefinition;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.luxons.sevenwonders.lobby.Lobby.GameAlreadyStartedException;
import org.luxons.sevenwonders.lobby.Lobby.PlayerNameAlreadyUsedException;
import org.luxons.sevenwonders.lobby.Lobby.PlayerOverflowException;
import org.luxons.sevenwonders.lobby.Lobby.PlayerUnderflowException;
import org.luxons.sevenwonders.lobby.Lobby.UnknownPlayerException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

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
        gameOwner = new Player("gameowner", "Game owner");
        lobby = new Lobby(0, "Test Game", gameOwner, gameDefinition);
    }

    @Test
    public void testId() {
        Lobby l = new Lobby(5, "Test Game", gameOwner, gameDefinition);
        assertEquals(5, l.getId());
    }

    @Test
    public void testName() {
        Lobby l = new Lobby(5, "Test Game", gameOwner, gameDefinition);
        assertEquals("Test Game", l.getName());
    }

    @Test
    public void testOwner() {
        gameOwner.setIndex(42);
        Lobby l = new Lobby(5, "Test Game", gameOwner, gameDefinition);
        assertSame(gameOwner, l.getPlayers().get(0));
        assertSame(l, gameOwner.getLobby());
        assertEquals(0, gameOwner.getIndex());
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
    public void isOwner_trueWhenOwnerUsername() {
        assertTrue(lobby.isOwner(gameOwner.getUsername()));
    }

    @Test
    public void isOwner_falseWhenOtherPlayerName() {
        Player player = new Player("testuser", "Test User");
        lobby.addPlayer(player);
        assertFalse(lobby.isOwner(player.getUsername()));
    }

    @Test
    public void addPlayer_success() {
        Player player = new Player("testuser", "Test User");
        lobby.addPlayer(player);
        assertTrue(lobby.containsUser("testuser"));
        assertSame(lobby, player.getLobby());
        assertSame(1, player.getIndex()); // the owner is 0
    }

    @Test(expected = PlayerNameAlreadyUsedException.class)
    public void addPlayer_failsOnSameName() {
        Player player = new Player("testuser", "Test User");
        Player player2 = new Player("testuser2", "Test User");
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
        lobby.startGame();
        lobby.addPlayer(new Player("soonerNextTime", "The Late Guy"));
    }

    private void addPlayers(int nbPlayers) {
        for (int i = 0; i < nbPlayers; i++) {
            Player player = new Player("testuser" + i, "Test User " + i);
            lobby.addPlayer(player);
        }
    }

    @Test(expected = UnknownPlayerException.class)
    public void removePlayer_failsWhenNotPresent() {
        lobby.removePlayer("anyname");
    }

    @Test
    public void removePlayer_success() {
        Player player = new Player("testuser", "Test User");
        lobby.addPlayer(player);
        lobby.removePlayer("testuser");
        assertFalse(lobby.containsUser("testuser"));
        assertNull(player.getLobby());
        assertNull(player.getGame());
        assertEquals(-1, player.getIndex());
    }

    @Test
    public void reorderPlayers_success() {
        Player player1 = new Player("testuser1", "Test User 1");
        Player player2 = new Player("testuser2", "Test User 2");
        Player player3 = new Player("testuser3", "Test User 3");
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);
        lobby.addPlayer(player3);
        lobby.reorderPlayers(Arrays.asList("testuser3", "testuser1", "testuser2"));
        assertEquals("testuser3", lobby.getPlayers().get(0).getUsername());
        assertEquals("testuser1", lobby.getPlayers().get(1).getUsername());
        assertEquals("testuser2", lobby.getPlayers().get(2).getUsername());
        assertEquals(0, lobby.getPlayers().get(0).getIndex());
        assertEquals(1, lobby.getPlayers().get(1).getIndex());
        assertEquals(2, lobby.getPlayers().get(2).getIndex());
    }

    @Test(expected = UnknownPlayerException.class)
    public void reorderPlayers_failsOnUnknownPlayer() {
        Player player1 = new Player("testuser1", "Test User 1");
        Player player2 = new Player("testuser2", "Test User 2");
        Player player3 = new Player("testuser3", "Test User 3");
        lobby.addPlayer(player1);
        lobby.addPlayer(player2);
        lobby.addPlayer(player3);
        lobby.reorderPlayers(Arrays.asList("unknown", "testuser1", "testuser2"));
    }

    @Theory
    public void startGame_failsBelowMinPlayers(int nbPlayers) {
        assumeTrue(nbPlayers < gameDefinition.getMinPlayers());
        thrown.expect(PlayerUnderflowException.class);
        // there is already the owner
        addPlayers(nbPlayers - 1);
        lobby.startGame();
    }

    @Theory
    public void startGame_succeedsAboveMinPlayers(int nbPlayers) {
        assumeTrue(nbPlayers >= gameDefinition.getMinPlayers());
        assumeTrue(nbPlayers <= gameDefinition.getMaxPlayers());
        // there is already the owner
        addPlayers(nbPlayers - 1);
        Game game = lobby.startGame();
        assertNotNull(game);
        lobby.getPlayers().forEach(p -> assertSame(game, p.getGame()));
    }

    @Test
    public void startGame_switchesState() {
        assertTrue(lobby.getState() == State.LOBBY);
        // there is already the owner
        addPlayers(gameDefinition.getMinPlayers() - 1);
        lobby.startGame();
        assertTrue(lobby.getState() == State.PLAYING);
    }

    @Test
    public void setSettings() throws Exception {
        CustomizableSettings settings = new CustomizableSettings();
        lobby.setSettings(settings);
        assertSame(settings, lobby.getSettings());
    }
}

package org.luxons.sevenwonders.lobby

import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.game.data.GameDefinition
import org.luxons.sevenwonders.game.data.GameDefinitionLoader
import org.luxons.sevenwonders.lobby.Lobby.*
import java.util.Arrays

@RunWith(Theories::class)
class LobbyTest {

    @JvmField
    @Rule
    var thrown = ExpectedException.none()

    private lateinit var gameOwner: Player

    private lateinit var lobby: Lobby

    @Before
    fun setUp() {
        gameOwner = Player("gameowner", "Game owner")
        lobby = Lobby(0, "Test Game", gameOwner, gameDefinition)
    }

    @Test
    fun testId() {
        val lobby = Lobby(5, "Test Game", gameOwner, gameDefinition)
        assertEquals(5, lobby.id)
    }

    @Test
    fun testName() {
        val lobby = Lobby(5, "Test Game", gameOwner, gameDefinition)
        assertEquals("Test Game", lobby.name)
    }

    @Test
    fun testOwner() {
        val lobby = Lobby(5, "Test Game", gameOwner, gameDefinition)
        assertSame(gameOwner, lobby.getPlayers()[0])
        assertSame(lobby, gameOwner.lobby)
    }

    @Test
    fun isOwner_falseWhenNull() {
        assertFalse(lobby.isOwner(null))
    }

    @Test
    fun isOwner_falseWhenEmptyString() {
        assertFalse(lobby.isOwner(""))
    }

    @Test
    fun isOwner_falseWhenGarbageString() {
        assertFalse(lobby.isOwner("this is garbage"))
    }

    @Test
    fun isOwner_trueWhenOwnerUsername() {
        assertTrue(lobby.isOwner(gameOwner.username))
    }

    @Test
    fun isOwner_falseWhenOtherPlayerName() {
        val player = Player("testuser", "Test User")
        lobby.addPlayer(player)
        assertFalse(lobby.isOwner(player.username))
    }

    @Test
    fun addPlayer_success() {
        val player = Player("testuser", "Test User")
        lobby.addPlayer(player)
        assertTrue(lobby.containsUser("testuser"))
        assertSame(lobby, player.lobby)
    }

    @Test(expected = PlayerNameAlreadyUsedException::class)
    fun addPlayer_failsOnSameName() {
        val player = Player("testuser", "Test User")
        val player2 = Player("testuser2", "Test User")
        lobby.addPlayer(player)
        lobby.addPlayer(player2)
    }

    @Test(expected = PlayerOverflowException::class)
    fun addPlayer_playerOverflowWhenTooMany() {
        // the owner + the max number gives an overflow
        addPlayers(gameDefinition.maxPlayers)
    }

    @Test(expected = GameAlreadyStartedException::class)
    fun addPlayer_failWhenGameStarted() {
        // total with owner is the minimum
        addPlayers(gameDefinition.minPlayers - 1)
        lobby.startGame()
        lobby.addPlayer(Player("soonerNextTime", "The Late Guy"))
    }

    private fun addPlayers(nbPlayers: Int) {
        for (i in 0 until nbPlayers) {
            val player = Player("testuser$i", "Test User $i")
            lobby.addPlayer(player)
        }
    }

    @Test(expected = UnknownPlayerException::class)
    fun removePlayer_failsWhenNotPresent() {
        lobby.removePlayer("anyname")
    }

    @Test
    fun removePlayer_success() {
        val player = Player("testuser", "Test User")
        lobby.addPlayer(player)
        assertTrue(player.isInLobby)
        assertFalse(player.isInGame)

        lobby.removePlayer("testuser")
        assertFalse(lobby.containsUser("testuser"))
        assertFalse(player.isInLobby)
        assertFalse(player.isInGame)
    }

    @Test
    fun reorderPlayers_success() {
        val player1 = Player("testuser1", "Test User 1")
        val player2 = Player("testuser2", "Test User 2")
        val player3 = Player("testuser3", "Test User 3")
        lobby.addPlayer(player1)
        lobby.addPlayer(player2)
        lobby.addPlayer(player3)
        lobby.reorderPlayers(Arrays.asList("testuser3", "testuser1", "testuser2"))
        assertEquals("testuser3", lobby.getPlayers()[0].username)
        assertEquals("testuser1", lobby.getPlayers()[1].username)
        assertEquals("testuser2", lobby.getPlayers()[2].username)
    }

    @Test(expected = UnknownPlayerException::class)
    fun reorderPlayers_failsOnUnknownPlayer() {
        val player1 = Player("testuser1", "Test User 1")
        val player2 = Player("testuser2", "Test User 2")
        val player3 = Player("testuser3", "Test User 3")
        lobby.addPlayer(player1)
        lobby.addPlayer(player2)
        lobby.addPlayer(player3)
        lobby.reorderPlayers(Arrays.asList("unknown", "testuser1", "testuser2"))
    }

    @Theory
    fun startGame_failsBelowMinPlayers(nbPlayers: Int) {
        assumeTrue(nbPlayers < gameDefinition.minPlayers)
        thrown.expect(PlayerUnderflowException::class.java)
        // there is already the owner
        addPlayers(nbPlayers - 1)
        lobby.startGame()
    }

    @Theory
    fun startGame_succeedsAboveMinPlayers(nbPlayers: Int) {
        assumeTrue(nbPlayers >= gameDefinition.minPlayers)
        assumeTrue(nbPlayers <= gameDefinition.maxPlayers)
        // there is already the owner
        addPlayers(nbPlayers - 1)

        assertEquals(nbPlayers, lobby.getPlayers().size)
        lobby.getPlayers().forEach {
            assertSame(lobby, it.lobby)
            assertTrue(it.isInLobby)
            assertFalse(it.isInGame)
        }

        val game = lobby.startGame()
        assertNotNull(game)
        lobby.getPlayers().forEachIndexed { index, it ->
            assertSame(index, it.index)
            assertSame(lobby, it.lobby)
            assertSame(game, it.game)
            assertTrue(it.isInLobby)
            assertTrue(it.isInGame)
        }
    }

    @Test
    fun startGame_switchesState() {
        assertTrue(lobby.state === State.LOBBY)
        // there is already the owner
        addPlayers(gameDefinition.minPlayers - 1)
        lobby.startGame()
        assertTrue(lobby.state === State.PLAYING)
    }

    @Test
    fun setSettings() {
        val settings = CustomizableSettings()
        lobby.settings = settings
        assertSame(settings, lobby.settings)
    }

    companion object {

        private lateinit var gameDefinition: GameDefinition

        @JvmStatic
        @DataPoints
        fun nbPlayers(): IntArray {
            return intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
        }

        @JvmStatic
        @BeforeClass
        fun loadDefinition() {
            gameDefinition = GameDefinitionLoader.gameDefinition
        }
    }
}

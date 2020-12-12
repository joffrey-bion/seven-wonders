package org.luxons.sevenwonders.server.lobby

import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.data.GameDefinition
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.server.lobby.Lobby.GameAlreadyStartedException
import org.luxons.sevenwonders.server.lobby.Lobby.PlayerListMismatchException
import org.luxons.sevenwonders.server.lobby.Lobby.PlayerNameAlreadyUsedException
import org.luxons.sevenwonders.server.lobby.Lobby.PlayerOverflowException
import org.luxons.sevenwonders.server.lobby.Lobby.PlayerUnderflowException
import org.luxons.sevenwonders.server.lobby.Lobby.UnknownPlayerException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(Theories::class)
class LobbyTest {

    private lateinit var gameOwner: Player

    private lateinit var lobby: Lobby

    @Before
    fun setUp() {
        gameOwner = Player("gameowner", "Game owner")
        lobby = Lobby(42, "Test Game", gameOwner, gameDefinition)
    }

    @Test
    fun testId() {
        assertEquals(42, lobby.id)
    }

    @Test
    fun testName() {
        assertEquals("Test Game", lobby.name)
    }

    @Test
    fun testOwner() {
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

    @Test
    fun addPlayer_failsOnSameName() {
        val player = Player("testuser", "Test User")
        val player2 = Player("testuser2", "Test User")
        lobby.addPlayer(player)
        assertFailsWith<PlayerNameAlreadyUsedException> {
            lobby.addPlayer(player2)
        }
    }

    @Test
    fun addPlayer_playerOverflowWhenTooMany() {
        assertFailsWith<PlayerOverflowException> {
            // the owner + the max number gives an overflow
            addPlayers(gameDefinition.maxPlayers)
        }
    }

    @Test
    fun addPlayer_failWhenGameStarted() {
        // total with owner is the minimum
        addPlayers(gameDefinition.minPlayers - 1)
        lobby.startGame()
        assertFailsWith<GameAlreadyStartedException> {
            lobby.addPlayer(Player("soonerNextTime", "The Late Guy"))
        }
    }

    private fun addPlayers(nbPlayers: Int) {
        repeat(nbPlayers) {
            val player = Player("testuser$it", "Test User $it")
            lobby.addPlayer(player)
        }
    }

    @Test
    fun removePlayer_failsWhenNotPresent() {
        assertFailsWith<UnknownPlayerException> {
            lobby.removePlayer("anyname")
        }
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

        val reorderedUsernames = listOf("testuser3", "gameowner", "testuser1", "testuser2")
        lobby.reorderPlayers(reorderedUsernames)

        assertEquals(reorderedUsernames, lobby.getPlayers().map { it.username })
    }

    @Test
    fun reorderPlayers_failsOnUnknownPlayer() {
        val player1 = Player("testuser1", "Test User 1")
        val player2 = Player("testuser2", "Test User 2")
        lobby.addPlayer(player1)
        lobby.addPlayer(player2)

        assertFailsWith<PlayerListMismatchException> {
            lobby.reorderPlayers(listOf("unknown", "testuser2", "gameowner"))
        }
    }

    @Test
    fun reorderPlayers_failsOnExtraPlayer() {
        val player1 = Player("testuser1", "Test User 1")
        val player2 = Player("testuser2", "Test User 2")
        lobby.addPlayer(player1)
        lobby.addPlayer(player2)

        assertFailsWith<PlayerListMismatchException> {
            lobby.reorderPlayers(listOf("testuser2", "onemore", "testuser1", "gameowner"))
        }
    }

    @Test
    fun reorderPlayers_failsOnMissingPlayer() {
        val player1 = Player("testuser1", "Test User 1")
        val player2 = Player("testuser2", "Test User 2")
        lobby.addPlayer(player1)
        lobby.addPlayer(player2)

        assertFailsWith<PlayerListMismatchException> {
            lobby.reorderPlayers(listOf("testuser2", "gameowner"))
        }
    }

    @Theory
    fun startGame_failsBelowMinPlayers(nbPlayers: Int) {
        assumeTrue(nbPlayers < gameDefinition.minPlayers)

        // there is already the owner
        addPlayers(nbPlayers - 1)

        assertFailsWith<PlayerUnderflowException> {
            lobby.startGame()
        }
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
        assertEquals(State.LOBBY, lobby.state)
        // there is already the owner
        addPlayers(gameDefinition.minPlayers - 1)
        lobby.startGame()
        assertEquals(State.PLAYING, lobby.state)
    }

    @Test
    fun setSettings() {
        val settings = Settings()
        lobby.settings = settings
        assertSame(settings, lobby.settings)
    }

    companion object {

        private lateinit var gameDefinition: GameDefinition

        @JvmStatic
        @DataPoints
        fun nbPlayers(): IntArray = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        @JvmStatic
        @BeforeClass
        fun loadDefinition() {
            gameDefinition = GameDefinition.load()
        }
    }
}

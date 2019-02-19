package org.luxons.sevenwonders.controllers

import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.actions.ReorderPlayersAction
import org.luxons.sevenwonders.actions.UpdateSettingsAction
import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.game.api.WonderSidePickMethod.ALL_A
import org.luxons.sevenwonders.lobby.Lobby
import org.luxons.sevenwonders.lobby.Player
import org.luxons.sevenwonders.lobby.PlayerIsNotOwnerException
import org.luxons.sevenwonders.lobby.PlayerNotInLobbyException
import org.luxons.sevenwonders.lobby.State
import org.luxons.sevenwonders.repositories.LobbyRepository
import org.luxons.sevenwonders.repositories.PlayerNotFoundException
import org.luxons.sevenwonders.repositories.PlayerRepository
import org.luxons.sevenwonders.test.TestUtils
import java.util.Arrays
import java.util.HashMap
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LobbyControllerTest {

    private lateinit var playerRepository: PlayerRepository

    private lateinit var lobbyRepository: LobbyRepository

    private lateinit var lobbyController: LobbyController

    @Before
    fun setUp() {
        val template = TestUtils.createSimpMessagingTemplate()
        playerRepository = PlayerRepository()
        lobbyRepository = LobbyRepository()
        lobbyController = LobbyController(lobbyRepository, playerRepository, template)
    }

    @Test
    fun init_succeeds() {
        val owner = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", owner)

        assertTrue(lobby.getPlayers().contains(owner))
        assertSame(lobby, owner.lobby)
        assertEquals(owner, lobby.owner)
        assertTrue(owner.isInLobby)
        assertFalse(owner.isInGame)
    }

    @Test(expected = PlayerNotFoundException::class)
    fun leave_failsWhenPlayerDoesNotExist() {
        val principal = TestPrincipal("I don't exist")
        lobbyController.leave(principal)
    }

    @Test(expected = PlayerNotInLobbyException::class)
    fun leave_failsWhenNotInLobby() {
        playerRepository.createOrUpdate("testuser", "Test User")
        val principal = TestPrincipal("testuser")
        lobbyController.leave(principal)
    }

    @Test
    fun leave_succeedsWhenInALobby_asOwner() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        val principal = TestPrincipal("testuser")
        lobbyController.leave(principal)

        assertFalse(lobbyRepository.list().contains(lobby))
        assertFalse(player.isInLobby)
        assertFalse(player.isInGame)
    }

    @Test
    fun leave_succeedsWhenInALobby_asPeasant() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)
        val player2 = addPlayer(lobby, "testuser2")

        val principal = TestPrincipal("testuser2")
        lobbyController.leave(principal)

        assertFalse(lobby.getPlayers().contains(player2))
        assertFalse(player2.isInLobby)
        assertFalse(player2.isInGame)
    }

    @Test
    fun reorderPlayers_succeedsForOwner() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        val player2 = addPlayer(lobby, "testuser2")
        val player3 = addPlayer(lobby, "testuser3")
        val player4 = addPlayer(lobby, "testuser4")

        val players = Arrays.asList(player, player2, player3, player4)
        assertEquals(players, lobby.getPlayers())

        val reorderedPlayers = Arrays.asList(player3, player, player2, player4)
        val playerNames = reorderedPlayers.map { it.username }
        val reorderPlayersAction = ReorderPlayersAction(playerNames)

        val principal = TestPrincipal("testuser")
        lobbyController.reorderPlayers(reorderPlayersAction, principal)

        assertEquals(reorderedPlayers, lobby.getPlayers())
    }

    @Test(expected = PlayerIsNotOwnerException::class)
    fun reorderPlayers_failsForPeasant() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        val player2 = addPlayer(lobby, "testuser2")
        val player3 = addPlayer(lobby, "testuser3")

        val reorderedPlayers = Arrays.asList(player3, player, player2)
        val playerNames = reorderedPlayers.map { it.username }
        val reorderPlayersAction = ReorderPlayersAction(playerNames)

        val principal = TestPrincipal("testuser2")
        lobbyController.reorderPlayers(reorderPlayersAction, principal)
    }

    @Test
    fun updateSettings_succeedsForOwner() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        addPlayer(lobby, "testuser2")
        addPlayer(lobby, "testuser3")
        addPlayer(lobby, "testuser4")

        assertEquals(CustomizableSettings(), lobby.settings)

        val newSettings = CustomizableSettings(12L, 5, ALL_A, 5, 5, 4, 10, 2, HashMap())
        val updateSettingsAction = UpdateSettingsAction(newSettings)

        val principal = TestPrincipal("testuser")
        lobbyController.updateSettings(updateSettingsAction, principal)

        assertEquals(newSettings, lobby.settings)
    }

    @Test(expected = PlayerIsNotOwnerException::class)
    fun updateSettings_failsForPeasant() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        addPlayer(lobby, "testuser2")
        addPlayer(lobby, "testuser3")

        val updateSettingsAction = UpdateSettingsAction(CustomizableSettings())

        val principal = TestPrincipal("testuser2")
        lobbyController.updateSettings(updateSettingsAction, principal)
    }

    @Test
    fun startGame_succeedsForOwner() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        addPlayer(lobby, "testuser2")
        addPlayer(lobby, "testuser3")
        addPlayer(lobby, "testuser4")

        val principal = TestPrincipal("testuser")
        lobbyController.startGame(principal)

        assertSame(State.PLAYING, lobby.state)
    }

    @Test(expected = PlayerIsNotOwnerException::class)
    fun startGame_failsForPeasant() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        addPlayer(lobby, "testuser2")
        addPlayer(lobby, "testuser3")

        val principal = TestPrincipal("testuser2")
        lobbyController.startGame(principal)
    }

    private fun addPlayer(lobby: Lobby, username: String): Player {
        val player = playerRepository.createOrUpdate(username, username)
        lobby.addPlayer(player)

        assertTrue(lobby.getPlayers().contains(player))
        assertSame(lobby, player.lobby)
        assertTrue(player.isInLobby)
        assertFalse(player.isInGame)
        return player
    }
}

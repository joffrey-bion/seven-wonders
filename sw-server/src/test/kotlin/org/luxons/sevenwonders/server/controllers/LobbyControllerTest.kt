package org.luxons.sevenwonders.server.controllers

import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.model.api.actions.ReorderPlayersAction
import org.luxons.sevenwonders.model.api.actions.UpdateSettingsAction
import org.luxons.sevenwonders.model.CustomizableSettings
import org.luxons.sevenwonders.model.WonderSidePickMethod.ALL_A
import org.luxons.sevenwonders.server.lobby.Lobby
import org.luxons.sevenwonders.server.lobby.Player
import org.luxons.sevenwonders.server.lobby.PlayerIsNotOwnerException
import org.luxons.sevenwonders.server.lobby.PlayerNotInLobbyException
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.server.repositories.LobbyRepository
import org.luxons.sevenwonders.server.repositories.PlayerNotFoundException
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.luxons.sevenwonders.server.test.mockSimpMessagingTemplate
import java.util.HashMap
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LobbyControllerTest {

    private lateinit var playerRepository: PlayerRepository

    private lateinit var lobbyRepository: LobbyRepository

    private lateinit var lobbyController: LobbyController

    @Before
    fun setUp() {
        val template = mockSimpMessagingTemplate()
        playerRepository = PlayerRepository()
        lobbyRepository = LobbyRepository()
        lobbyController = LobbyController(lobbyRepository, playerRepository, template, "UNUSED")
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

    @Test
    fun leave_failsWhenPlayerDoesNotExist() {
        val principal = TestPrincipal("I don't exist")

        assertFailsWith<PlayerNotFoundException> {
            lobbyController.leave(principal)
        }
    }

    @Test
    fun leave_failsWhenNotInLobby() {
        playerRepository.createOrUpdate("testuser", "Test User")
        val principal = TestPrincipal("testuser")

        assertFailsWith<PlayerNotInLobbyException> {
            lobbyController.leave(principal)
        }
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

        val players = listOf(player, player2, player3, player4)
        assertEquals(players, lobby.getPlayers())

        val reorderedPlayers = listOf(player3, player, player2, player4)
        val playerNames = reorderedPlayers.map { it.username }
        val reorderPlayersAction = ReorderPlayersAction(playerNames)

        val principal = TestPrincipal("testuser")
        lobbyController.reorderPlayers(reorderPlayersAction, principal)

        assertEquals(reorderedPlayers, lobby.getPlayers())
    }

    @Test
    fun reorderPlayers_failsForPeasant() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        val player2 = addPlayer(lobby, "testuser2")
        val player3 = addPlayer(lobby, "testuser3")

        val reorderedPlayers = listOf(player3, player, player2)
        val playerNames = reorderedPlayers.map { it.username }
        val reorderPlayersAction = ReorderPlayersAction(playerNames)

        val principal = TestPrincipal("testuser2")

        assertFailsWith<PlayerIsNotOwnerException> {
            lobbyController.reorderPlayers(reorderPlayersAction, principal)
        }
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

    @Test
    fun updateSettings_failsForPeasant() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        addPlayer(lobby, "testuser2")
        addPlayer(lobby, "testuser3")

        val updateSettingsAction = UpdateSettingsAction(CustomizableSettings())

        val principal = TestPrincipal("testuser2")

        assertFailsWith<PlayerIsNotOwnerException> {
            lobbyController.updateSettings(updateSettingsAction, principal)
        }
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

    @Test
    fun startGame_failsForPeasant() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val lobby = lobbyRepository.create("Test Game", player)

        addPlayer(lobby, "testuser2")
        addPlayer(lobby, "testuser3")

        val principal = TestPrincipal("testuser2")

        assertFailsWith<PlayerIsNotOwnerException> {
            lobbyController.startGame(principal)
        }
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

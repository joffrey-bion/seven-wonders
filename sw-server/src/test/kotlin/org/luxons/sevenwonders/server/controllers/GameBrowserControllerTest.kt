package org.luxons.sevenwonders.server.controllers

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.model.api.GameListEvent
import org.luxons.sevenwonders.model.api.actions.CreateGameAction
import org.luxons.sevenwonders.model.api.actions.JoinGameAction
import org.luxons.sevenwonders.server.controllers.GameBrowserController.UserAlreadyInGameException
import org.luxons.sevenwonders.server.repositories.LobbyRepository
import org.luxons.sevenwonders.server.repositories.PlayerNotFoundException
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.luxons.sevenwonders.server.test.mockSimpMessagingTemplate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameBrowserControllerTest {

    private lateinit var playerRepository: PlayerRepository

    private lateinit var gameBrowserController: GameBrowserController

    @Before
    fun setUp() {
        val meterRegistry = SimpleMeterRegistry()
        playerRepository = PlayerRepository(meterRegistry)
        val lobbyRepository = LobbyRepository(meterRegistry)
        val template = mockSimpMessagingTemplate()
        val lobbyController = LobbyController(lobbyRepository, playerRepository, template, "UNUSED", meterRegistry)
        gameBrowserController = GameBrowserController(lobbyController, lobbyRepository, playerRepository, template)
    }

    @Test
    fun listGames_initiallyEmpty() {
        val principal = TestPrincipal("testuser")
        val gameListEvent = gameBrowserController.listGames(principal).event as GameListEvent.ReplaceList
        assertTrue(gameListEvent.lobbies.isEmpty())
    }

    @Test
    fun createGame_success() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val principal = TestPrincipal("testuser")

        val action = CreateGameAction("Test Game")

        val createdLobby = gameBrowserController.createGame(action, principal)

        assertEquals("Test Game", createdLobby.name)

        val gameListEvent = gameBrowserController.listGames(principal).event as GameListEvent.ReplaceList
        assertFalse(gameListEvent.lobbies.isEmpty())
        val lobby = gameListEvent.lobbies.first()
        assertEquals(lobby, createdLobby)
        assertEquals(player.username, lobby.players[0].username)
    }

    @Test
    fun createGame_failsForUnknownPlayer() {
        val principal = TestPrincipal("unknown")
        val action = CreateGameAction("Test Game")

        assertFailsWith<PlayerNotFoundException> {
            gameBrowserController.createGame(action, principal)
        }
    }

    @Test
    fun createGame_failsWhenAlreadyInGame() {
        playerRepository.createOrUpdate("testuser", "Test User")
        val principal = TestPrincipal("testuser")

        val createGameAction1 = CreateGameAction("Test Game 1")

        // auto-enters the game
        gameBrowserController.createGame(createGameAction1, principal)

        val createGameAction2 = CreateGameAction("Test Game 2")

        // already in a game
        assertFailsWith<UserAlreadyInGameException> {
            gameBrowserController.createGame(createGameAction2, principal)
        }
    }

    @Test
    fun joinGame_success() {
        val owner = playerRepository.createOrUpdate("testowner", "Test User Owner")
        val ownerPrincipal = TestPrincipal("testowner")
        val createGameAction = CreateGameAction("Test Game")

        val createdLobby = gameBrowserController.createGame(createGameAction, ownerPrincipal)
        assertEquals(owner.username, createdLobby.players[0].username)

        val joiner = playerRepository.createOrUpdate("testjoiner", "Test User Joiner")
        val joinerPrincipal = TestPrincipal("testjoiner")
        val joinGameAction = JoinGameAction(createdLobby.id)

        val joinedLobby = gameBrowserController.joinGame(joinGameAction, joinerPrincipal)

        assertEquals(owner.username, joinedLobby.players[0].username)
        assertEquals(joiner.username, joinedLobby.players[1].username)
    }

    @Test
    fun joinGame_failsWhenAlreadyInGame() {
        playerRepository.createOrUpdate("testowner", "Test User Owner")
        val ownerPrincipal = TestPrincipal("testowner")
        val createGameAction = CreateGameAction("Test Game")

        val createdLobby = gameBrowserController.createGame(createGameAction, ownerPrincipal)

        playerRepository.createOrUpdate("testjoiner", "Test User Joiner")
        val joinerPrincipal = TestPrincipal("testjoiner")
        val joinGameAction = JoinGameAction(createdLobby.id)

        // joins the game
        gameBrowserController.joinGame(joinGameAction, joinerPrincipal)
        // should fail because already in a game
        assertFailsWith<UserAlreadyInGameException> {
            gameBrowserController.joinGame(joinGameAction, joinerPrincipal)
        }
    }
}

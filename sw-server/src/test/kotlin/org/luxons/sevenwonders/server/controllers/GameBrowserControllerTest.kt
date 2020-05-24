package org.luxons.sevenwonders.server.controllers

import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.model.api.actions.CreateGameAction
import org.luxons.sevenwonders.model.api.actions.JoinGameAction
import org.luxons.sevenwonders.server.api.toDTO
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
        playerRepository = PlayerRepository()
        val lobbyRepository = LobbyRepository()
        val template = mockSimpMessagingTemplate()
        val lobbyController = LobbyController(lobbyRepository, playerRepository, template, "UNUSED")
        gameBrowserController = GameBrowserController(lobbyController, lobbyRepository, playerRepository, template)
    }

    @Test
    fun listGames_initiallyEmpty() {
        val principal = TestPrincipal("testuser")
        val games = gameBrowserController.listGames(principal)
        assertTrue(games.isEmpty())
    }

    @Test
    fun createGame_success() {
        val player = playerRepository.createOrUpdate("testuser", "Test User")
        val principal = TestPrincipal("testuser")

        val action = CreateGameAction("Test Game")

        val createdLobby = gameBrowserController.createGame(action, principal)

        assertEquals("Test Game", createdLobby.name)

        val games = gameBrowserController.listGames(principal)
        assertFalse(games.isEmpty())
        val lobby = games.iterator().next()
        assertEquals(lobby, createdLobby)
        assertEquals(player.toDTO(), lobby.players[0])
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
        assertEquals(owner.toDTO(), createdLobby.players[0])

        val joiner = playerRepository.createOrUpdate("testjoiner", "Test User Joiner")
        val joinerPrincipal = TestPrincipal("testjoiner")
        val joinGameAction = JoinGameAction(createdLobby.id)

        val joinedLobby = gameBrowserController.joinGame(joinGameAction, joinerPrincipal)

        assertEquals(owner.toDTO(), joinedLobby.players[0])
        assertEquals(joiner.toDTO(), joinedLobby.players[1])
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

package org.luxons.sevenwonders.server.controllers

import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.luxons.sevenwonders.model.api.actions.CreateGameAction
import org.luxons.sevenwonders.model.api.actions.JoinGameAction
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.model.api.events.GameListEvent
import org.luxons.sevenwonders.server.controllers.GameBrowserController.UserAlreadyInGameException
import org.luxons.sevenwonders.server.repositories.LobbyRepository
import org.luxons.sevenwonders.server.repositories.PlayerNotFoundException
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.luxons.sevenwonders.server.test.MockMessageChannel
import org.luxons.sevenwonders.server.test.expectSentGameEventTo
import org.luxons.sevenwonders.server.test.expectSentGameListEvent
import org.springframework.messaging.simp.SimpMessagingTemplate
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GameBrowserControllerTest {

    private lateinit var messageChannel: MockMessageChannel

    private lateinit var playerRepository: PlayerRepository

    private lateinit var gameBrowserController: GameBrowserController

    @BeforeTest
    fun setUp() {
        messageChannel = MockMessageChannel()
        val messenger = SimpMessagingTemplate(messageChannel)
        val meterRegistry = SimpleMeterRegistry()
        val lobbyRepository = LobbyRepository(meterRegistry)
        playerRepository = PlayerRepository(meterRegistry)
        val lobbyController = LobbyController(messenger, lobbyRepository, playerRepository, "UNUSED", meterRegistry)
        gameBrowserController = GameBrowserController(messenger, lobbyController, lobbyRepository, playerRepository)
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

        gameBrowserController.createGame(action, principal)

        val createdEvent = messageChannel.expectSentGameListEvent<GameListEvent.CreateOrUpdate>()
        val lobbyJoinedEvent = messageChannel.expectSentGameEventTo<GameEvent.LobbyJoined>("testuser")
        val createdLobby = createdEvent.lobby

        assertEquals("Test Game", createdLobby.name)
        assertEquals(createdLobby, lobbyJoinedEvent.lobby)

        messageChannel.expectNoMoreMessages()

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

        gameBrowserController.createGame(createGameAction, ownerPrincipal)

        val createdEvent = messageChannel.expectSentGameListEvent<GameListEvent.CreateOrUpdate>()
        messageChannel.expectSentGameEventTo<GameEvent.LobbyJoined>("testowner")
        val createdLobby = createdEvent.lobby
        assertEquals(owner.username, createdLobby.players[0].username)

        messageChannel.expectNoMoreMessages()

        val joiner = playerRepository.createOrUpdate("testjoiner", "Test User Joiner")
        val joinerPrincipal = TestPrincipal("testjoiner")
        val joinGameAction = JoinGameAction(createdLobby.id)

        gameBrowserController.joinGame(joinGameAction, joinerPrincipal)

        // lobby update for existing players
        messageChannel.expectSentGameEventTo<GameEvent.LobbyUpdated>("testowner")
        messageChannel.expectSentGameEventTo<GameEvent.LobbyUpdated>("testjoiner")
        // lobby update for people on game browser page
        messageChannel.expectSentGameListEvent<GameListEvent.CreateOrUpdate>()
        // lobby joined for the player who joined
        val joinedLobbyEvent = messageChannel.expectSentGameEventTo<GameEvent.LobbyJoined>("testjoiner")
        val joinedLobby = joinedLobbyEvent.lobby

        assertEquals(owner.username, joinedLobby.players[0].username)
        assertEquals(joiner.username, joinedLobby.players[1].username)

        messageChannel.expectNoMoreMessages()
    }

    @Test
    fun joinGame_failsWhenAlreadyInGame() {
        playerRepository.createOrUpdate("testowner", "Test User Owner")
        val ownerPrincipal = TestPrincipal("testowner")
        val createGameAction = CreateGameAction("Test Game")

        gameBrowserController.createGame(createGameAction, ownerPrincipal)

        val createdEvent = messageChannel.expectSentGameListEvent<GameListEvent.CreateOrUpdate>()
        messageChannel.expectSentGameEventTo<GameEvent.LobbyJoined>("testowner")
        val createdLobby = createdEvent.lobby

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

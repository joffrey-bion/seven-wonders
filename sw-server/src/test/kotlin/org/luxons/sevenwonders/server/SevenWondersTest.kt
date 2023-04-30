package org.luxons.sevenwonders.server

import org.junit.runner.RunWith
import org.luxons.sevenwonders.client.*
import org.luxons.sevenwonders.model.TurnAction
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.model.api.events.GameListEvent
import org.luxons.sevenwonders.server.test.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SevenWondersTest {

    @LocalServerPort
    private val randomServerPort: Int = 0

    private suspend fun connectNewClient(): SevenWondersSession {
        val client = SevenWondersClient()
        val serverUrl = "ws://localhost:$randomServerPort"
        return client.connect(serverUrl)
    }

    private suspend fun disconnect(vararg sessions: SevenWondersSession) {
        for (session in sessions) {
            session.disconnect()
        }
    }

    @Test
    fun chooseName_succeedsWithCorrectDisplayName() = runAsyncTest {
        val session = connectNewClient()
        val playerName = "Test User"
        val player = session.chooseNameAndAwait(playerName)
        assertEquals(playerName, player.displayName)
        session.disconnect()
    }

    private suspend fun newPlayer(name: String): SevenWondersSession = connectNewClient().apply {
        chooseNameAndAwait(name)
    }

    @Test
    fun lobbySubscription_ignoredForOutsiders() = runAsyncTest {
        val ownerSession = newPlayer("GameOwner")
        val session1 = newPlayer("Player1")
        val session2 = newPlayer("Player2")
        val gameName = "Test Game"

        val lobby = ownerSession.createGameAndAwaitLobby(gameName)

        session1.joinGameAndAwaitLobby(lobby.id)
        session2.joinGameAndAwaitLobby(lobby.id)

        val outsiderSession = newPlayer("Outsider")
        val outsiderAsserter = outsiderSession.eventAsserter(scope = this)

        val ownerAsserter = ownerSession.eventAsserter(scope = this)
        ownerSession.startGame()
        ownerAsserter.expectGameEvent<GameEvent.GameStarted>()
        outsiderAsserter.expectNoGameEvent("outsider should not receive the game start event of this game")

        disconnect(ownerSession, session1, session2, outsiderSession)
    }

    @Test
    fun createGame_success() = runAsyncTest {
        val ownerSession = newPlayer("GameOwner")

        val gameName = "Test Game"
        val lobby = ownerSession.createGameAndAwaitLobby(gameName)
        assertEquals(gameName, lobby.name)

        disconnect(ownerSession)
    }

    @Test
    fun createGame_seenByConnectedPlayers() = runAsyncTest {
        val otherSession = newPlayer("OtherPlayer")
        val asserter = otherSession.eventAsserter(scope = this)

        val initialListEvent = asserter.expectGameListEvent<GameListEvent.ReplaceList>()
        assertEquals(0, initialListEvent.lobbies.size)

        val ownerSession = newPlayer("GameOwner")
        val gameName = "Test Game"
        val createdLobby = ownerSession.createGameAndAwaitLobby(gameName)

        val afterGameListEvent = asserter.expectGameListEvent<GameListEvent.CreateOrUpdate>()
        val receivedLobby = afterGameListEvent.lobby
        assertEquals(createdLobby.id, receivedLobby.id)
        assertEquals(createdLobby.name, receivedLobby.name)

        disconnect(ownerSession, otherSession)
    }

    @Test
    fun startGame_3players() = runAsyncTest {
        val session1 = newPlayer("Player1")
        val session2 = newPlayer("Player2")

        val asserter1 = session1.eventAsserter(scope = this)
        val lobby = session1.createGameAndAwaitLobby("Test Game")
        asserter1.expectGameEvent<GameEvent.LobbyJoined>()

        val asserter2 = session2.eventAsserter(scope = this)
        session2.joinGame(lobby.id)
        asserter1.expectGameEvent<GameEvent.LobbyUpdated>()
        asserter2.expectGameEvent<GameEvent.LobbyUpdated>()
        asserter2.expectGameEvent<GameEvent.LobbyJoined>()

        // player 3 connects after game creation (on purpose)
        val session3 = newPlayer("Player3")
        val asserter3 = session3.eventAsserter(scope = this)
        session3.joinGame(lobby.id)
        asserter1.expectGameEvent<GameEvent.LobbyUpdated>()
        asserter2.expectGameEvent<GameEvent.LobbyUpdated>()
        asserter3.expectGameEvent<GameEvent.LobbyUpdated>()
        asserter3.expectGameEvent<GameEvent.LobbyJoined>()

        session1.startGame()
        asserter1.expectGameEvent<GameEvent.GameStarted>()
        asserter2.expectGameEvent<GameEvent.GameStarted>()
        asserter3.expectGameEvent<GameEvent.GameStarted>()

        session2.sayReady()
        asserter2.expectNoGameEvent("nothing should happen while other players are not ready for game start")
        asserter1.expectNoGameEvent("nothing should happen while other players are not ready for game start")
        asserter3.expectNoGameEvent("nothing should happen while other players are not ready for game start")

        session1.sayReady()
        asserter1.expectNoGameEvent("nothing should happen while other players are not ready for game start")
        session3.sayReady()

        asserter1.expectPlayFromHandTurn()
        asserter2.expectPlayFromHandTurn()
        asserter3.expectPlayFromHandTurn()

        session1.disconnect()
        session2.disconnect()
        session3.disconnect()
    }
}

private suspend fun EventAsserter.expectPlayFromHandTurn() {
    val firstTurn = expectGameEvent<GameEvent.NewTurnStarted>()
    val action = firstTurn.turnInfo.action
    assertTrue(action is TurnAction.PlayFromHand)
}

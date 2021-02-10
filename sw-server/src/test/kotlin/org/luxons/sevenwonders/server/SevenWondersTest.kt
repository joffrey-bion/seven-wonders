package org.luxons.sevenwonders.server

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.runner.RunWith
import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.client.createGameAndWaitLobby
import org.luxons.sevenwonders.client.joinGameAndWaitLobby
import org.luxons.sevenwonders.model.Action
import org.luxons.sevenwonders.model.api.GameListEvent
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.server.test.runAsyncTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.*

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
        val player = session.chooseName(playerName)
        assertEquals(playerName, player.displayName)
        session.disconnect()
    }

    private suspend fun newPlayer(name: String): SevenWondersSession = connectNewClient().apply {
        chooseName(name)
    }

    @Test
    fun lobbySubscription_ignoredForOutsiders() = runAsyncTest {
        val ownerSession = newPlayer("GameOwner")
        val session1 = newPlayer("Player1")
        val session2 = newPlayer("Player2")
        val gameName = "Test Game"

        val lobby = ownerSession.createGameWithLegacySettingsAndWaitLobby(gameName)

        session1.joinGameAndWaitLobby(lobby.id)
        session2.joinGameAndWaitLobby(lobby.id)

        val outsiderSession = newPlayer("Outsider")
        val gameStartedEvents = outsiderSession.watchGameStarted()
        ownerSession.startGame()

        val nullForTimeout = withTimeoutOrNull(50) { gameStartedEvents.first() }
        assertNull(nullForTimeout, "outsider should not receive the game start event of this game")

        disconnect(ownerSession, session1, session2, outsiderSession)
    }

    @Test
    fun createGame_success() = runAsyncTest {
        val ownerSession = newPlayer("GameOwner")

        val gameName = "Test Game"
        val lobby = ownerSession.createGameWithLegacySettingsAndWaitLobby(gameName)
        assertEquals(gameName, lobby.name)

        disconnect(ownerSession)
    }

    @Test
    fun createGame_seenByConnectedPlayers() = runAsyncTest {
        val otherSession = newPlayer("OtherPlayer")
        val games = otherSession.watchGames().produceIn(this)

        val initialListEvent = withTimeout(500) { games.receive() }
        assertTrue(initialListEvent is GameListEvent.ReplaceList)
        assertEquals(0, initialListEvent.lobbies.size)

        val ownerSession = newPlayer("GameOwner")
        val gameName = "Test Game"
        val createdLobby = ownerSession.createGameWithLegacySettingsAndWaitLobby(gameName)

        val afterGameListEvent = withTimeout(500) { games.receive() }
        assertTrue(afterGameListEvent is GameListEvent.CreateOrUpdate)
        val receivedLobby = afterGameListEvent.lobby
        assertEquals(createdLobby.id, receivedLobby.id)
        assertEquals(createdLobby.name, receivedLobby.name)

        disconnect(ownerSession, otherSession)
    }

    @Test
    fun startGame_3players() = runAsyncTest {
        val session1 = newPlayer("Player1")
        val session2 = newPlayer("Player2")

        val startEvents1 = session1.watchGameStarted()
        val lobby = session1.createGameWithLegacySettingsAndWaitLobby("Test Game")

        val startEvents2 = session2.watchGameStarted()
        session2.joinGameAndWaitLobby(lobby.id)

        // player 3 connects after game creation (on purpose)
        val session3 = newPlayer("Player3")
        val startEvents3 = session3.watchGameStarted()
        session3.joinGameAndWaitLobby(lobby.id)

        session1.startGame()

        listOf(
            session1 to startEvents1,
            session2 to startEvents2,
            session3 to startEvents3,
        ).forEach { (session, startEvents) ->
            launch {
                val initialReadyTurn = startEvents.first()
                assertEquals(Action.SAY_READY, initialReadyTurn.action)
                assertNull(initialReadyTurn.hand)
                val turns = session.watchTurns()
                session.sayReady()

                val firstActualTurn = turns.first()
                assertNotNull(firstActualTurn.hand)
                session.disconnect()
            }
        }
    }
}

private suspend fun SevenWondersSession.createGameWithLegacySettingsAndWaitLobby(gameName: String): LobbyDTO {
    val lobby = createGameAndWaitLobby(gameName)
    updateSettings(lobby.settings.copy(askForReadiness = true))
    return lobby
}

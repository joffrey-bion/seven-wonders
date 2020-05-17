package org.luxons.sevenwonders.server

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.produceIn
import org.junit.runner.RunWith
import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.server.test.runAsyncTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.*

@OptIn(FlowPreview::class)
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SevenWondersTest {

    @LocalServerPort
    private val randomServerPort: Int = 0

    private suspend fun connectNewClient(): SevenWondersSession {
        val client = SevenWondersClient()
        val serverUrl = "localhost:$randomServerPort"
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
        assertNotNull(player)
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
        val lobby = ownerSession.createGame(gameName)
        session1.joinGame(lobby.id)
        session2.joinGame(lobby.id)

        val outsiderSession = newPlayer("Outsider")
        val started = launch { outsiderSession.awaitGameStart(lobby.id) }

        ownerSession.startGame()
        val nothing = withTimeoutOrNull(30) { started.join() }
        assertNull(nothing)
        started.cancel()
        disconnect(ownerSession, session1, session2, outsiderSession)
    }

    @Test
    fun createGame_success() = runAsyncTest {
        val ownerSession = newPlayer("GameOwner")

        val gameName = "Test Game"
        val lobby = ownerSession.createGame(gameName)
        assertNotNull(lobby)
        assertEquals(gameName, lobby.name)

        disconnect(ownerSession)
    }

    @Test
    fun createGame_seenByConnectedPlayers() = runAsyncTest {
        val otherSession = newPlayer("OtherPlayer")
        val games = otherSession.watchGames().produceIn(this)

        var receivedLobbies = withTimeout(500) { games.receive() }
        assertNotNull(receivedLobbies)
        assertEquals(0, receivedLobbies.size)

        val ownerSession = newPlayer("GameOwner")
        val gameName = "Test Game"
        val createdLobby = ownerSession.createGame(gameName)

        receivedLobbies = withTimeout(500) { games.receive() }
        assertNotNull(receivedLobbies)
        assertEquals(1, receivedLobbies.size)
        val receivedLobby = receivedLobbies[0]
        assertEquals(createdLobby.id, receivedLobby.id)
        assertEquals(createdLobby.name, receivedLobby.name)

        disconnect(ownerSession, otherSession)
    }

    @Test
    fun startGame_3players() = runAsyncTest {
        val session1 = newPlayer("Player1")
        val session2 = newPlayer("Player2")

        val lobby = session1.createGame("Test Game")
        session2.joinGame(lobby.id)

        val session3 = newPlayer("Player3")
        session3.joinGame(lobby.id)

        listOf(session1, session2, session3).forEach { session ->
            launch {
                session.awaitGameStart(lobby.id)
                val turns = session.watchTurns().produceIn(this)
                delay(100) // ensures the subscription happened
                session.sayReady()
                val turn = turns.receive()
                assertNotNull(turn)
                session.disconnect()
            }
        }
        session1.startGame()
    }
}

package org.luxons.sevenwonders.server

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.client.SevenWondersSession
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SevenWondersTest {

    @LocalServerPort
    private val randomServerPort: Int = 0

    private lateinit var client: SevenWondersClient

    private lateinit var serverUrl: String

    @Before
    fun setUpClientAndUrl() {
        client = SevenWondersClient()
        serverUrl = "ws://localhost:$randomServerPort"
    }

    private suspend fun disconnect(vararg sessions: SevenWondersSession) {
        for (session in sessions) {
            session.disconnect()
        }
    }

    @Test
    fun chooseName() {
        runBlocking {
            val session = client.connect(serverUrl)
            val playerName = "Test User"
            val player = session.chooseName(playerName)
            assertNotNull(player)
            assertEquals(playerName, player.displayName)
            session.disconnect()
        }
    }

    private suspend fun newPlayer(name: String): SevenWondersSession = client.connect(serverUrl).apply {
        chooseName(name)
    }

    @Test
    fun lobbySubscription_ignoredForOutsiders() {
        runBlocking {
            val ownerSession = newPlayer("GameOwner")
            val session1 = newPlayer("Player1")
            val session2 = newPlayer("Player2")
            val gameName = "Test Game"
            val lobby = ownerSession.createGame(gameName)
            session1.joinGame(lobby.id)
            session2.joinGame(lobby.id)

            val outsiderSession = newPlayer("Outsider")
            val (started) = outsiderSession.watchGameStart(lobby.id)

            ownerSession.startGame(lobby.id)
            val nothing = withTimeoutOrNull(30) { started.receive() }
            assertNull(nothing)
            disconnect(ownerSession, session1, session2, outsiderSession)
        }
    }

    @Test
    fun createGame_success() {
        runBlocking {
            val ownerSession = newPlayer("GameOwner")

            val gameName = "Test Game"
            val lobby = ownerSession.createGame(gameName)
            assertNotNull(lobby)
            assertEquals(gameName, lobby.name)

            disconnect(ownerSession)
        }
    }

    @Test
    fun createGame_seenByConnectedPlayers() {
        runBlocking {
            val otherSession = newPlayer("OtherPlayer")
            val (games) = otherSession.watchGames()

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
    }

    @Test
    fun startGame_3players() = runBlocking {
        val session1 = newPlayer("Player1")
        val session2 = newPlayer("Player2")

        val lobby = session1.createGame("Test Game")
        session2.joinGame(lobby.id)

        val session3 = newPlayer("Player3")
        session3.joinGame(lobby.id)

        session1.startGame(lobby.id)

        val (turns1) = session1.watchTurns()
        val (turns2) = session2.watchTurns()
        val (turns3) = session3.watchTurns()
        session1.sayReady()
        session2.sayReady()
        session3.sayReady()
        val turn1 = turns1.receive()
        val turn2 = turns2.receive()
        val turn3 = turns3.receive()
        assertNotNull(turn1)
        assertNotNull(turn2)
        assertNotNull(turn3)

        disconnect(session1, session2, session3)
    }
}

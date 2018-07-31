package org.luxons.sevenwonders

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.luxons.sevenwonders.test.api.SevenWondersClient
import org.luxons.sevenwonders.test.api.SevenWondersSession
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit4.SpringRunner
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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

    private fun disconnect(vararg sessions: SevenWondersSession) {
        for (session in sessions) {
            session.disconnect()
        }
    }

    @Test
    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    fun chooseName() {
        val session = client.connect(serverUrl)
        val playerName = "Test User"
        val player = session.chooseName(playerName)
        assertNotNull(player)
        assertEquals(playerName, player.displayName)
        session.disconnect()
    }

    @Throws(InterruptedException::class, TimeoutException::class, ExecutionException::class)
    private fun newPlayer(name: String): SevenWondersSession {
        val otherSession = client.connect(serverUrl)
        otherSession.chooseName(name)
        return otherSession
    }

    @Test
    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    fun lobbySubscription_ignoredForOutsiders() {
        val ownerSession = newPlayer("GameOwner")
        val session1 = newPlayer("Player1")
        val session2 = newPlayer("Player2")
        val gameName = "Test Game"
        val lobby = ownerSession.createGame(gameName)
        session1.joinGame(lobby.id)
        session2.joinGame(lobby.id)

        val outsiderSession = newPlayer("Outsider")
        val session = outsiderSession.jackstompSession
        val started = session.subscribeEmptyMsgs("/topic/lobby/" + lobby.id + "/started")

        ownerSession.startGame(lobby.id)
        val nothing = started.next(1, TimeUnit.SECONDS)
        assertNull(nothing)
        disconnect(ownerSession, session1, session2, outsiderSession)
    }

    @Test
    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    fun createGame_success() {
        val ownerSession = newPlayer("GameOwner")

        val gameName = "Test Game"
        val lobby = ownerSession.createGame(gameName)
        assertNotNull(lobby)
        assertEquals(gameName, lobby.name)

        disconnect(ownerSession)
    }

    @Test
    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    fun createGame_seenByConnectedPlayers() {
        val otherSession = newPlayer("OtherPlayer")
        val games = otherSession.watchGames()

        var receivedLobbies = games.next()
        assertNotNull(receivedLobbies)
        assertEquals(0, receivedLobbies.size.toLong())

        val ownerSession = newPlayer("GameOwner")
        val gameName = "Test Game"
        val createdLobby = ownerSession.createGame(gameName)

        receivedLobbies = games.next()
        assertNotNull(receivedLobbies)
        assertEquals(1, receivedLobbies.size.toLong())
        val receivedLobby = receivedLobbies[0]
        assertEquals(createdLobby.id, receivedLobby.id)
        assertEquals(createdLobby.name, receivedLobby.name)

        disconnect(ownerSession, otherSession)
    }

    @Test
    @Throws(Exception::class)
    fun startGame_3players() {
        val session1 = newPlayer("Player1")
        val session2 = newPlayer("Player2")

        val lobby = session1.createGame("Test Game")
        session2.joinGame(lobby.id)

        val session3 = newPlayer("Player3")
        session3.joinGame(lobby.id)

        session1.startGame(lobby.id)

        val turns1 = session1.watchTurns()
        val turns2 = session2.watchTurns()
        val turns3 = session3.watchTurns()
        session1.sayReady()
        session2.sayReady()
        session3.sayReady()
        val turn1 = turns1.next()
        val turn2 = turns2.next()
        val turn3 = turns3.next()
        assertNotNull(turn1)
        assertNotNull(turn2)
        assertNotNull(turn3)

        disconnect(session1, session2, session3)
    }

    @After
    fun tearDown() {
        client.stop()
    }
}

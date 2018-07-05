package org.luxons.sevenwonders.test.api

import org.hildan.jackstomp.Channel
import org.hildan.jackstomp.JackstompSession
import org.junit.Assert.*
import org.luxons.sevenwonders.actions.ChooseNameAction
import org.luxons.sevenwonders.actions.CreateGameAction
import org.luxons.sevenwonders.actions.JoinGameAction
import org.luxons.sevenwonders.errors.ErrorDTO

class SevenWondersSession(val jackstompSession: JackstompSession) {

    fun disconnect() {
        jackstompSession.disconnect()
    }

    fun watchErrors(): Channel<ErrorDTO> {
        return jackstompSession.subscribe("/user/queue/errors", ErrorDTO::class.java)
    }

    @Throws(InterruptedException::class)
    fun chooseName(displayName: String): ApiPlayer {
        val action = ChooseNameAction(displayName)
        return jackstompSession.request(action, ApiPlayer::class.java, "/app/chooseName", "/user/queue/nameChoice")
    }

    fun watchGames(): Channel<Array<ApiLobby>> {
        return jackstompSession.subscribe("/topic/games", Array<ApiLobby>::class.java)
    }

    @Throws(InterruptedException::class)
    fun createGame(gameName: String): ApiLobby {
        val action = CreateGameAction(gameName)
        return jackstompSession.request(action, ApiLobby::class.java, "/app/lobby/create", "/user/queue/lobby/joined")
    }

    @Throws(InterruptedException::class)
    fun joinGame(gameId: Long): ApiLobby {
        val action = JoinGameAction(gameId)
        val lobby =
            jackstompSession.request(action, ApiLobby::class.java, "/app/lobby/join", "/user/queue/lobby/joined")
        assertNotNull(lobby)
        assertEquals(gameId, lobby.id)
        return lobby
    }

    fun watchLobbyUpdates(gameId: Long): Channel<ApiLobby> {
        return jackstompSession.subscribe("/topic/lobby/$gameId/updated", ApiLobby::class.java)
    }

    fun watchLobbyStart(gameId: Long): Channel<ApiLobby> {
        return jackstompSession.subscribe("/topic/lobby/$gameId/started", ApiLobby::class.java)
    }

    @Throws(InterruptedException::class)
    fun startGame(gameId: Long) {
        val sendDestination = "/app/lobby/startGame"
        val receiveDestination = "/topic/lobby/$gameId/started"
        val received = jackstompSession.request(null, sendDestination, receiveDestination)
        assertTrue(received)
    }

    fun sayReady() {
        jackstompSession.send("/app/game/sayReady", "")
    }

    fun watchTurns(): Channel<ApiPlayerTurnInfo> {
        return jackstompSession.subscribe("/user/queue/game/turn", ApiPlayerTurnInfo::class.java)
    }
}

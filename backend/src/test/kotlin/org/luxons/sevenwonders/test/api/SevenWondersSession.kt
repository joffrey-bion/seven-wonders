package org.luxons.sevenwonders.test.api

import org.hildan.jackstomp.Channel
import org.hildan.jackstomp.JackstompSession
import org.luxons.sevenwonders.actions.ChooseNameAction
import org.luxons.sevenwonders.actions.CreateGameAction
import org.luxons.sevenwonders.actions.JoinGameAction
import org.luxons.sevenwonders.api.LobbyDTO
import org.luxons.sevenwonders.api.PlayerDTO
import org.luxons.sevenwonders.errors.ErrorDTO
import org.luxons.sevenwonders.game.api.PlayerTurnInfo
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SevenWondersSession(val jackstompSession: JackstompSession) {

    fun disconnect() {
        jackstompSession.disconnect()
    }

    fun watchErrors(): Channel<ErrorDTO> = jackstompSession.subscribe("/user/queue/errors", ErrorDTO::class.java)

    @Throws(InterruptedException::class)
    fun chooseName(displayName: String): PlayerDTO {
        val action = ChooseNameAction(displayName)
        return jackstompSession.request(action, PlayerDTO::class.java, "/app/chooseName", "/user/queue/nameChoice")
    }

    fun watchGames(): Channel<Array<LobbyDTO>> {
        return jackstompSession.subscribe("/topic/games", Array<LobbyDTO>::class.java)
    }

    @Throws(InterruptedException::class)
    fun createGame(gameName: String): LobbyDTO {
        val action = CreateGameAction(gameName)
        return jackstompSession.request(action, LobbyDTO::class.java, "/app/lobby/create", "/user/queue/lobby/joined")
    }

    @Throws(InterruptedException::class)
    fun joinGame(gameId: Long): LobbyDTO {
        val action = JoinGameAction(gameId)
        val lobby =
            jackstompSession.request(action, LobbyDTO::class.java, "/app/lobby/join", "/user/queue/lobby/joined")
        assertNotNull(lobby)
        assertEquals(gameId, lobby.id)
        return lobby
    }

    fun watchLobbyUpdates(gameId: Long): Channel<LobbyDTO> =
        jackstompSession.subscribe("/topic/lobby/$gameId/updated", LobbyDTO::class.java)

    fun watchLobbyStart(gameId: Long): Channel<LobbyDTO> =
        jackstompSession.subscribe("/topic/lobby/$gameId/started", LobbyDTO::class.java)

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

    fun watchTurns(): Channel<PlayerTurnInfo> =
        jackstompSession.subscribe("/user/queue/game/turn", PlayerTurnInfo::class.java)
}

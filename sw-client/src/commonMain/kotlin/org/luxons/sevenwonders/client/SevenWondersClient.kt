package org.luxons.sevenwonders.client

import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.list
import kotlinx.serialization.serializer
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSubscription
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.conversions.kxserialization.withJsonConversions
import org.hildan.krossbow.stomp.sendEmptyMsg
import org.hildan.krossbow.stomp.subscribeEmptyMsg
import org.luxons.sevenwonders.model.CustomizableSettings
import org.luxons.sevenwonders.model.GameState
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.SEVEN_WONDERS_WS_ENDPOINT
import org.luxons.sevenwonders.model.api.actions.ChooseNameAction
import org.luxons.sevenwonders.model.api.actions.CreateGameAction
import org.luxons.sevenwonders.model.api.actions.JoinGameAction
import org.luxons.sevenwonders.model.api.actions.ReorderPlayersAction
import org.luxons.sevenwonders.model.api.actions.UpdateSettingsAction
import org.luxons.sevenwonders.model.api.errors.ErrorDTO
import org.luxons.sevenwonders.model.cards.PreparedCard

class SevenWondersClient {

    private val stompClient = StompClient()

    suspend fun connect(serverHost: String): SevenWondersSession {
        val session = stompClient.connect("ws://$serverHost$SEVEN_WONDERS_WS_ENDPOINT").withJsonConversions()
        return SevenWondersSession(session)
    }
}

@OptIn(ImplicitReflectionSerializer::class)
suspend inline fun <reified T : Any, reified U : Any> StompSessionWithKxSerialization.request(
    sendDestination: String,
    receiveDestination: String,
    payload: T? = null
): U {
    val sub = subscribe<U>(receiveDestination)
    convertAndSend(sendDestination, payload)
    val msg = sub.messages.receive()
    sub.unsubscribe()
    return msg
}

class SevenWondersSession(private val stompSession: StompSessionWithKxSerialization) {

    suspend fun disconnect() = stompSession.disconnect()

    suspend fun watchErrors(): StompSubscription<ErrorDTO> =
        stompSession.subscribe("/user/queue/errors", ErrorDTO.serializer())

    suspend fun chooseName(displayName: String): PlayerDTO {
        val action = ChooseNameAction(displayName)
        return stompSession.request("/app/chooseName", "/user/queue/nameChoice", action)
    }

    suspend fun watchGames(): StompSubscription<List<LobbyDTO>> =
        stompSession.subscribe("/topic/games", LobbyDTO.serializer().list)

    suspend fun createGame(gameName: String): LobbyDTO {
        val action = CreateGameAction(gameName)
        return stompSession.request("/app/lobby/create", "/user/queue/lobby/joined", action)
    }

    suspend fun joinGame(gameId: Long): LobbyDTO {
        val action = JoinGameAction(gameId)
        return stompSession.request("/app/lobby/join", "/user/queue/lobby/joined", action)
    }

    suspend fun leaveGame() {
        stompSession.sendEmptyMsg("/app/lobby/leave")
    }

    suspend fun reorderPlayers(players: List<String>) {
        stompSession.convertAndSend("/app/lobby/reorderPlayers", ReorderPlayersAction(players), ReorderPlayersAction.serializer())
    }

    suspend fun updateSettings(settings: CustomizableSettings) {
        stompSession.convertAndSend("/app/lobby/reorderPlayers", UpdateSettingsAction(settings), UpdateSettingsAction.serializer())
    }

    suspend fun watchLobbyUpdates(): StompSubscription<LobbyDTO> =
        stompSession.subscribe("/user/queue/lobby/updated", LobbyDTO.serializer())

    suspend fun awaitGameStart(gameId: Long) {
        val gameStartSubscription = stompSession.subscribeEmptyMsg("/topic/lobby/$gameId/started")
        gameStartSubscription.messages.receive()
        gameStartSubscription.unsubscribe()
    }

    suspend fun startGame() {
        stompSession.sendEmptyMsg("/app/lobby/startGame")
    }

    suspend fun watchPlayerReady(gameId: Long): StompSubscription<String> =
            stompSession.subscribe("/topic/game/$gameId/playerReady", String.serializer())

    suspend fun watchTableUpdates(gameId: Long): StompSubscription<GameState> =
            stompSession.subscribe("/topic/game/$gameId/tableUpdates", GameState.serializer())

    suspend fun watchPreparedCards(gameId: Long): StompSubscription<PreparedCard> =
            stompSession.subscribe("/topic/game/$gameId/prepared", PreparedCard.serializer())

    suspend fun watchTurns(): StompSubscription<PlayerTurnInfo> =
            stompSession.subscribe("/user/queue/game/turn", PlayerTurnInfo.serializer())

    suspend fun sayReady() {
        stompSession.sendEmptyMsg("/app/game/sayReady")
    }
}

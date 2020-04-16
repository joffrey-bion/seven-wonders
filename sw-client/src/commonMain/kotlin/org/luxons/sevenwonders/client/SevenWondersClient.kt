package org.luxons.sevenwonders.client

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSubscription
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.withJsonConversions
import org.hildan.krossbow.stomp.sendEmptyMsg
import org.luxons.sevenwonders.model.CustomizableSettings
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.SEVEN_WONDERS_WS_ENDPOINT
import org.luxons.sevenwonders.model.api.actions.ChooseNameAction
import org.luxons.sevenwonders.model.api.actions.CreateGameAction
import org.luxons.sevenwonders.model.api.actions.JoinGameAction
import org.luxons.sevenwonders.model.api.actions.PrepareMoveAction
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

private suspend inline fun <reified T : Any, reified U : Any> StompSessionWithKxSerialization.request(
    sendDestination: String,
    receiveDestination: String,
    payload: T? = null,
    serializer: SerializationStrategy<T>,
    deserializer: DeserializationStrategy<U>
): U {
    val sub = subscribe(receiveDestination, deserializer)
    convertAndSend(sendDestination, payload, serializer)
    val msg = sub.messages.receive()
    sub.unsubscribe()
    return msg
}

class SevenWondersSession(private val stompSession: StompSessionWithKxSerialization) {

    suspend fun disconnect() = stompSession.disconnect()

    suspend fun watchErrors(): StompSubscription<ErrorDTO> =
        stompSession.subscribe("/user/queue/errors", ErrorDTO.serializer())

    suspend fun chooseName(displayName: String): ConnectedPlayer = stompSession.request(
        sendDestination = "/app/chooseName",
        receiveDestination = "/user/queue/nameChoice",
        payload = ChooseNameAction(displayName),
        serializer = ChooseNameAction.serializer(),
        deserializer = ConnectedPlayer.serializer()
    )

    suspend fun watchGames(): StompSubscription<List<LobbyDTO>> =
        stompSession.subscribe("/topic/games", LobbyDTO.serializer().list)

    suspend fun createGame(gameName: String): LobbyDTO = stompSession.request(
        sendDestination = "/app/lobby/create",
        receiveDestination = "/user/queue/lobby/joined",
        payload = CreateGameAction(gameName),
        serializer = CreateGameAction.serializer(),
        deserializer = LobbyDTO.serializer()
    )

    suspend fun joinGame(gameId: Long): LobbyDTO = stompSession.request(
        sendDestination = "/app/lobby/join",
        receiveDestination = "/user/queue/lobby/joined",
        payload = JoinGameAction(gameId),
        serializer = JoinGameAction.serializer(),
        deserializer = LobbyDTO.serializer()
    )

    suspend fun leaveLobby() {
        stompSession.sendEmptyMsg("/app/lobby/leave")
    }

    suspend fun reorderPlayers(players: List<String>) {
        stompSession.convertAndSend("/app/lobby/reorderPlayers", ReorderPlayersAction(players), ReorderPlayersAction.serializer())
    }

    suspend fun updateSettings(settings: CustomizableSettings) {
        stompSession.convertAndSend("/app/lobby/updateSettings", UpdateSettingsAction(settings), UpdateSettingsAction.serializer())
    }

    suspend fun watchLobbyUpdates(): StompSubscription<LobbyDTO> =
        stompSession.subscribe("/user/queue/lobby/updated", LobbyDTO.serializer())

    suspend fun awaitGameStart(gameId: Long): PlayerTurnInfo {
        val gameStartSubscription = stompSession.subscribe("/user/queue/lobby/$gameId/started", PlayerTurnInfo.serializer())
        val turnInfo = gameStartSubscription.messages.receive()
        gameStartSubscription.unsubscribe()
        return turnInfo
    }

    suspend fun startGame() {
        stompSession.sendEmptyMsg("/app/lobby/startGame")
    }

    suspend fun watchPlayerReady(gameId: Long): StompSubscription<String> =
            stompSession.subscribe("/topic/game/$gameId/playerReady", String.serializer())

    suspend fun watchPreparedCards(gameId: Long): StompSubscription<PreparedCard> =
            stompSession.subscribe("/topic/game/$gameId/prepared", PreparedCard.serializer())

    suspend fun watchTurns(): StompSubscription<PlayerTurnInfo> =
            stompSession.subscribe("/user/queue/game/turn", PlayerTurnInfo.serializer())

    suspend fun sayReady() {
        stompSession.sendEmptyMsg("/app/game/sayReady")
    }

    suspend fun prepareMove(move: PlayerMove): PlayerMove = stompSession.request(
        sendDestination = "/app/game/prepareMove",
        receiveDestination = "/user/queue/game/preparedMove",
        payload = PrepareMoveAction(move),
        serializer = PrepareMoveAction.serializer(),
        deserializer = PlayerMove.serializer()
    )

    suspend fun unprepareMove() {
        stompSession.sendEmptyMsg("/app/game/unprepareMove")
    }
}

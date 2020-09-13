package org.luxons.sevenwonders.client

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.config.HeartBeatTolerance
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.conversions.kxserialization.withJsonConversions
import org.hildan.krossbow.stomp.sendEmptyMsg
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.SEVEN_WONDERS_WS_ENDPOINT
import org.luxons.sevenwonders.model.api.actions.AddBotAction
import org.luxons.sevenwonders.model.api.actions.ChooseNameAction
import org.luxons.sevenwonders.model.api.actions.CreateGameAction
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.model.api.actions.JoinGameAction
import org.luxons.sevenwonders.model.api.actions.PrepareMoveAction
import org.luxons.sevenwonders.model.api.actions.ReassignWondersAction
import org.luxons.sevenwonders.model.api.actions.ReorderPlayersAction
import org.luxons.sevenwonders.model.api.actions.UpdateSettingsAction
import org.luxons.sevenwonders.model.api.errors.ErrorDTO
import org.luxons.sevenwonders.model.cards.PreparedCard
import org.luxons.sevenwonders.model.wonders.AssignedWonder

class SevenWondersClient {

    private val stompClient = StompClient {
        heartBeat = HeartBeat(10000, 10000)
        heartBeatTolerance = HeartBeatTolerance(0, 10000) // wide margin to account for heroku cold start
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun connect(serverUrl: String): SevenWondersSession {
        val session = stompClient.connect("$serverUrl$SEVEN_WONDERS_WS_ENDPOINT").withJsonConversions()
        return SevenWondersSession(session)
    }
}

// TODO replace these calls by actual HTTP endpoints
@OptIn(ExperimentalCoroutinesApi::class)
private suspend inline fun <reified T : Any, reified U : Any> StompSessionWithKxSerialization.request(
    sendDestination: String,
    receiveDestination: String,
    payload: T? = null,
    serializer: SerializationStrategy<T>,
    deserializer: DeserializationStrategy<U>,
): U {
    val sub = subscribe(receiveDestination, deserializer)
    convertAndSend(sendDestination, payload, serializer)
    return sub.first()
}

class SevenWondersSession(private val stompSession: StompSessionWithKxSerialization) {

    suspend fun disconnect() = stompSession.disconnect()

    suspend fun watchErrors(): Flow<ErrorDTO> = stompSession.subscribe("/user/queue/errors", ErrorDTO.serializer())

    suspend fun chooseName(displayName: String, icon: Icon? = null): ConnectedPlayer = stompSession.request(
        sendDestination = "/app/chooseName",
        receiveDestination = "/user/queue/nameChoice",
        payload = ChooseNameAction(displayName, icon),
        serializer = ChooseNameAction.serializer(),
        deserializer = ConnectedPlayer.serializer(),
    )

    suspend fun watchGames(): Flow<List<LobbyDTO>> =
        stompSession.subscribe("/topic/games", ListSerializer(LobbyDTO.serializer()))

    suspend fun createGame(gameName: String): LobbyDTO = stompSession.request(
        sendDestination = "/app/lobby/create",
        receiveDestination = "/user/queue/lobby/joined",
        payload = CreateGameAction(gameName),
        serializer = CreateGameAction.serializer(),
        deserializer = LobbyDTO.serializer(),
    )

    suspend fun joinGame(gameId: Long): LobbyDTO = stompSession.request(
        sendDestination = "/app/lobby/join",
        receiveDestination = "/user/queue/lobby/joined",
        payload = JoinGameAction(gameId),
        serializer = JoinGameAction.serializer(),
        deserializer = LobbyDTO.serializer(),
    )

    suspend fun leaveLobby() {
        stompSession.sendEmptyMsg("/app/lobby/leave")
    }

    suspend fun addBot(displayName: String) {
        stompSession.convertAndSend("/app/lobby/addBot", AddBotAction(displayName), AddBotAction.serializer())
    }

    suspend fun reorderPlayers(players: List<String>) {
        stompSession.convertAndSend(
            destination = "/app/lobby/reorderPlayers",
            body = ReorderPlayersAction(players),
            serializer = ReorderPlayersAction.serializer(),
        )
    }

    suspend fun reassignWonders(wonders: List<AssignedWonder>) {
        stompSession.convertAndSend(
            destination = "/app/lobby/reassignWonders",
            body = ReassignWondersAction(wonders),
            serializer = ReassignWondersAction.serializer(),
        )
    }

    suspend fun updateSettings(settings: Settings) {
        stompSession.convertAndSend(
            destination = "/app/lobby/updateSettings",
            body = UpdateSettingsAction(settings),
            serializer = UpdateSettingsAction.serializer(),
        )
    }

    suspend fun watchLobbyUpdates(): Flow<LobbyDTO> =
        stompSession.subscribe("/user/queue/lobby/updated", LobbyDTO.serializer())

    suspend fun awaitGameStart(gameId: Long): PlayerTurnInfo {
        val startEvents = stompSession.subscribe("/user/queue/lobby/$gameId/started", PlayerTurnInfo.serializer())
        return startEvents.first()
    }

    suspend fun startGame() {
        stompSession.sendEmptyMsg("/app/lobby/startGame")
    }

    suspend fun watchPlayerReady(gameId: Long): Flow<String> =
        stompSession.subscribe("/topic/game/$gameId/playerReady", String.serializer())

    suspend fun watchPreparedCards(gameId: Long): Flow<PreparedCard> =
        stompSession.subscribe("/topic/game/$gameId/prepared", PreparedCard.serializer())

    suspend fun watchTurns(): Flow<PlayerTurnInfo> =
        stompSession.subscribe("/user/queue/game/turn", PlayerTurnInfo.serializer())

    suspend fun sayReady() {
        stompSession.sendEmptyMsg("/app/game/sayReady")
    }

    suspend fun watchOwnMoves(): Flow<PlayerMove> =
        stompSession.subscribe("/user/queue/game/preparedMove", PlayerMove.serializer())

    suspend fun prepareMove(move: PlayerMove) {
        stompSession.convertAndSend(
            destination = "/app/game/prepareMove",
            body = PrepareMoveAction(move),
            serializer = PrepareMoveAction.serializer(),
        )
    }

    suspend fun unprepareMove() {
        stompSession.sendEmptyMsg("/app/game/unprepareMove")
    }

    suspend fun leaveGame() {
        stompSession.sendEmptyMsg("/app/game/leave")
    }
}

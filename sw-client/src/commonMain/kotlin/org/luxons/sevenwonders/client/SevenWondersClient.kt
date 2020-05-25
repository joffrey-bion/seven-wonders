package org.luxons.sevenwonders.client

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.list
import kotlinx.serialization.builtins.serializer
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.conversions.kxserialization.withJsonConversions
import org.hildan.krossbow.stomp.sendEmptyMsg
import org.luxons.sevenwonders.model.CustomizableSettings
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.SEVEN_WONDERS_WS_ENDPOINT
import org.luxons.sevenwonders.model.api.actions.*
import org.luxons.sevenwonders.model.api.errors.ErrorDTO
import org.luxons.sevenwonders.model.cards.PreparedCard

class SevenWondersClient {

    private val stompClient = StompClient()

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
    deserializer: DeserializationStrategy<U>
): U = coroutineScope {
    val sub = async(start = CoroutineStart.UNDISPATCHED) {
        subscribe(receiveDestination, deserializer).first()
    }
    delay(30) // ensures the subscription happened
    convertAndSend(sendDestination, payload, serializer)
    sub.await()
}

class SevenWondersSession(private val stompSession: StompSessionWithKxSerialization) {

    suspend fun disconnect() = stompSession.disconnect()

    fun watchErrors(): Flow<ErrorDTO> = stompSession.subscribe("/user/queue/errors", ErrorDTO.serializer())

    suspend fun chooseName(displayName: String, icon: Icon? = null): ConnectedPlayer = stompSession.request(
        sendDestination = "/app/chooseName",
        receiveDestination = "/user/queue/nameChoice",
        payload = ChooseNameAction(displayName, icon),
        serializer = ChooseNameAction.serializer(),
        deserializer = ConnectedPlayer.serializer()
    )

    fun watchGames(): Flow<List<LobbyDTO>> =
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

    suspend fun addBot(displayName: String) {
        stompSession.convertAndSend("/app/lobby/addBot", AddBotAction(displayName), AddBotAction.serializer())
    }

    suspend fun reorderPlayers(players: List<String>) {
        stompSession.convertAndSend("/app/lobby/reorderPlayers", ReorderPlayersAction(players), ReorderPlayersAction.serializer())
    }

    suspend fun updateSettings(settings: CustomizableSettings) {
        stompSession.convertAndSend("/app/lobby/updateSettings", UpdateSettingsAction(settings), UpdateSettingsAction.serializer())
    }

    fun watchLobbyUpdates(): Flow<LobbyDTO> =
        stompSession.subscribe("/user/queue/lobby/updated", LobbyDTO.serializer())

    suspend fun awaitGameStart(gameId: Long): PlayerTurnInfo {
        val startEvents = stompSession.subscribe("/user/queue/lobby/$gameId/started", PlayerTurnInfo.serializer())
        return startEvents.first()
    }

    suspend fun startGame() {
        stompSession.sendEmptyMsg("/app/lobby/startGame")
    }

    fun watchPlayerReady(gameId: Long): Flow<String> =
            stompSession.subscribe("/topic/game/$gameId/playerReady", String.serializer())

    fun watchPreparedCards(gameId: Long): Flow<PreparedCard> =
            stompSession.subscribe("/topic/game/$gameId/prepared", PreparedCard.serializer())

    fun watchTurns(): Flow<PlayerTurnInfo> =
            stompSession.subscribe("/user/queue/game/turn", PlayerTurnInfo.serializer())

    suspend fun sayReady() {
        stompSession.sendEmptyMsg("/app/game/sayReady")
    }

    fun watchOwnMoves(): Flow<PlayerMove> =
        stompSession.subscribe(destination = "/user/queue/game/preparedMove", deserializer = PlayerMove.serializer())

    suspend fun prepareMove(move: PlayerMove) {
        stompSession.convertAndSend(
            destination = "/app/game/prepareMove",
            body = PrepareMoveAction(move),
            serializer = PrepareMoveAction.serializer()
        )
    }

    suspend fun unprepareMove() {
        stompSession.sendEmptyMsg("/app/game/unprepareMove")
    }

    suspend fun leaveGame() {
        stompSession.sendEmptyMsg("/app/game/leave")
    }
}

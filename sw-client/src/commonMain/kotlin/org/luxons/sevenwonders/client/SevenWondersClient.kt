package org.luxons.sevenwonders.client

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.config.HeartBeat
import org.hildan.krossbow.stomp.config.HeartBeatTolerance
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.convertAndSend
import org.hildan.krossbow.stomp.conversions.kxserialization.subscribe
import org.hildan.krossbow.stomp.conversions.kxserialization.withJsonConversions
import org.hildan.krossbow.stomp.sendEmptyMsg
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.api.*
import org.luxons.sevenwonders.model.api.actions.*
import org.luxons.sevenwonders.model.api.errors.ErrorDTO
import org.luxons.sevenwonders.model.api.events.*
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SevenWondersClient {

    private val stompClient = StompClient {
        heartBeat = HeartBeat(10.seconds, 10.seconds)
        heartBeatTolerance = HeartBeatTolerance(Duration.ZERO, 10.seconds) // wide margin to account for heroku cold start
    }

    suspend fun connect(serverUrl: String): SevenWondersSession {
        val session = stompClient.connect("$serverUrl$SEVEN_WONDERS_WS_ENDPOINT").withJsonConversions()
        return SevenWondersSession(session)
    }
}

class SevenWondersSession(private val stompSession: StompSessionWithKxSerialization) {

    suspend fun disconnect() = stompSession.disconnect()

    suspend fun watchErrors(): Flow<ErrorDTO> = stompSession.subscribe("/user/queue/errors", ErrorDTO.serializer())

    suspend fun watchGames(): Flow<GameListEvent> =
        stompSession.subscribe("/topic/games", GameListEventWrapper.serializer()).map { it.event }

    suspend fun watchGameEvents(): Flow<GameEvent> =
        stompSession.subscribe("/user/queue/game/events", GameEventWrapper.serializer()).map { it.event }

    suspend fun chooseName(displayName: String, icon: Icon? = null, isHuman: Boolean = true) {
        stompSession.convertAndSend(
            destination = "/app/chooseName",
            body = ChooseNameAction(displayName, icon, isHuman),
            serializer = ChooseNameAction.serializer(),
        )
    }

    suspend fun createGame(gameName: String) {
        stompSession.convertAndSend("/app/lobby/create", CreateGameAction(gameName), CreateGameAction.serializer())
    }

    suspend fun joinGame(gameId: Long) {
        stompSession.convertAndSend("/app/lobby/join", JoinGameAction(gameId), JoinGameAction.serializer())
    }

    suspend fun leaveLobby() {
        stompSession.sendEmptyMsg("/app/lobby/leave")
    }

    suspend fun disbandLobby() {
        stompSession.sendEmptyMsg("/app/lobby/disband")
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

    suspend fun startGame() {
        stompSession.sendEmptyMsg("/app/lobby/startGame")
    }

    suspend fun sayReady() {
        stompSession.sendEmptyMsg("/app/game/sayReady")
    }

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

suspend fun SevenWondersSession.chooseNameAndAwait(displayName: String, icon: Icon? = null, isHuman: Boolean = true): ConnectedPlayer {
    return doAndWaitForEvent(
        send = { chooseName(displayName, icon, isHuman) },
        subscribe = { watchNameChoice() }
    )
}

suspend fun SevenWondersSession.createGameAndAwaitLobby(gameName: String): LobbyDTO = doAndWaitForEvent(
    send = { createGame(gameName) },
    subscribe = { watchLobbyJoined() },
)

suspend fun SevenWondersSession.joinGameAndAwaitLobby(gameId: Long): LobbyDTO = doAndWaitForEvent(
    send = { joinGame(gameId) },
    subscribe = { watchLobbyJoined() },
)

private suspend fun <T> doAndWaitForEvent(send: suspend () -> Unit, subscribe: suspend () -> Flow<T>): T =
    coroutineScope {
        val eventsFlow = subscribe()
        val deferredFirstEvent = async(start = CoroutineStart.UNDISPATCHED) { eventsFlow.first() }
        send()
        deferredFirstEvent.await()
    }

suspend fun SevenWondersSession.watchTurns() =
    watchGameEvents().filterIsInstance<GameEvent.NewTurnStarted>().map { it.turnInfo }

suspend fun SevenWondersSession.watchLobbyJoined(): Flow<LobbyDTO> =
    watchGameEvents().filterIsInstance<GameEvent.LobbyJoined>().map { it.lobby }

suspend fun SevenWondersSession.watchNameChoice(): Flow<ConnectedPlayer> =
    watchGameEvents().filterIsInstance<GameEvent.NameChosen>().map { it.player }

package org.luxons.sevenwonders.client

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
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
import org.luxons.sevenwonders.model.api.*
import org.luxons.sevenwonders.model.api.actions.*
import org.luxons.sevenwonders.model.api.errors.ErrorDTO
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.model.api.events.GameEventWrapper
import org.luxons.sevenwonders.model.wonders.AssignedWonder

class SevenWondersClient {

    private val stompClient = StompClient {
        heartBeat = HeartBeat(10000, 10000)
        heartBeatTolerance = HeartBeatTolerance(0, 10000) // wide margin to account for heroku cold start
    }

    suspend fun connect(serverUrl: String): SevenWondersSession {
        val session = stompClient.connect("$serverUrl$SEVEN_WONDERS_WS_ENDPOINT").withJsonConversions()
        return SevenWondersSession(session)
    }
}

class SevenWondersSession(private val stompSession: StompSessionWithKxSerialization) {

    suspend fun disconnect() = stompSession.disconnect()

    suspend fun watchErrors(): Flow<ErrorDTO> = stompSession.subscribe("/user/queue/errors", ErrorDTO.serializer())

    suspend fun chooseName(displayName: String, icon: Icon? = null, isHuman: Boolean = true): ConnectedPlayer {
        return doAndWaitForEvent(
            send = {
                stompSession.convertAndSend(
                    destination = "/app/chooseName",
                    body = ChooseNameAction(displayName, icon, isHuman),
                    serializer = ChooseNameAction.serializer(),
                )
            },
            subscribe = {
                stompSession.subscribe(
                    destination = "/user/queue/nameChoice",
                    deserializer = ConnectedPlayer.serializer(),
                )
            }
        )
    }

    suspend fun watchGames(): Flow<GameListEvent> =
        stompSession.subscribe("/topic/games", GameListEventWrapper.serializer()).map { it.event }

    suspend fun createGame(gameName: String) {
        stompSession.convertAndSend("/app/lobby/create", CreateGameAction(gameName), CreateGameAction.serializer())
    }

    suspend fun joinGame(gameId: Long) {
        stompSession.convertAndSend("/app/lobby/join", JoinGameAction(gameId), JoinGameAction.serializer())
    }

    suspend fun watchLobbyJoined(): Flow<LobbyDTO> =
        stompSession.subscribe("/user/queue/lobby/joined", LobbyDTO.serializer())

    suspend fun leaveLobby() {
        stompSession.sendEmptyMsg("/app/lobby/leave")
    }

    suspend fun disbandLobby() {
        stompSession.sendEmptyMsg("/app/lobby/disband")
    }

    suspend fun watchLobbyLeft(): Flow<Long> = stompSession.subscribe("/user/queue/lobby/left", Long.serializer())

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

    suspend fun watchGameStarted(): Flow<PlayerTurnInfo> =
        stompSession.subscribe("/user/queue/lobby/started", PlayerTurnInfo.serializer())

    suspend fun startGame() {
        stompSession.sendEmptyMsg("/app/lobby/startGame")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun watchGameEvents(gameId: Long): Flow<GameEvent> {
        val private = watchPublicGameEvents()
        val public = watchPrivateGameEvents(gameId)
        return merge(private, public)
    }

    private suspend fun watchPrivateGameEvents(gameId: Long) =
        stompSession.subscribe("/topic/game/$gameId/events", GameEventWrapper.serializer()).map { it.event }

    suspend fun watchPublicGameEvents() =
        stompSession.subscribe("/user/queue/game/events", GameEventWrapper.serializer()).map { it.event }

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

suspend fun SevenWondersSession.createGameAndAwaitLobby(gameName: String): LobbyDTO = doAndWaitForEvent(
    send = { createGame(gameName) },
    subscribe = { watchLobbyJoined() },
)

suspend fun SevenWondersSession.joinGameAndAwaitLobby(gameId: Long): LobbyDTO = doAndWaitForEvent(
    send = { joinGame(gameId) },
    subscribe = { watchLobbyJoined() },
)

suspend fun SevenWondersSession.startGameAndAwaitFirstTurn(): PlayerTurnInfo = doAndWaitForEvent(
    send = { startGame() },
    subscribe = { watchGameStarted() },
)

suspend fun SevenWondersSession.joinGameAndAwaitFirstTurn(gameId: Long): PlayerTurnInfo = doAndWaitForEvent(
    send = { joinGame(gameId) },
    subscribe = { watchGameStarted() },
)

suspend fun SevenWondersSession.leaveGameAndAwaitEnd() = doAndWaitForEvent(
    send = { leaveGame() },
    subscribe = { watchLobbyLeft() },
)

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun <T> doAndWaitForEvent(send: suspend () -> Unit, subscribe: suspend () -> Flow<T>): T =
    coroutineScope {
        val eventsFlow = subscribe()
        val deferredFirstEvent = async(start = CoroutineStart.UNDISPATCHED) { eventsFlow.first() }
        send()
        deferredFirstEvent.await()
    }

suspend fun SevenWondersSession.watchTurns() =
    watchPublicGameEvents().filterIsInstance<GameEvent.NewTurnStarted>().map { it.turnInfo }

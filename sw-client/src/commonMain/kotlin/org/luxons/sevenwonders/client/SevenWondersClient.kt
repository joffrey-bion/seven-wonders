package org.luxons.sevenwonders.client

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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
import org.luxons.sevenwonders.model.cards.PreparedCard
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
        val sub = stompSession.subscribe(
            destination = "/user/queue/nameChoice",
            deserializer = ConnectedPlayer.serializer(),
        )
        stompSession.convertAndSend(
            destination = "/app/chooseName",
            body = ChooseNameAction(displayName, icon, isHuman),
            serializer = ChooseNameAction.serializer(),
        )
        return sub.first()
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

suspend fun SevenWondersSession.createGameAndWaitLobby(gameName: String): LobbyDTO = coroutineScope {
    val lobbies = watchLobbyJoined()
    val joinedLobby = async { lobbies.first() }
    createGame(gameName)
    joinedLobby.await()
}

suspend fun SevenWondersSession.joinGameAndWaitLobby(gameId: Long): LobbyDTO = coroutineScope {
    val lobbies = watchLobbyJoined()
    val joinedLobby = async { lobbies.first() }
    joinGame(gameId)
    joinedLobby.await()
}

suspend fun SevenWondersSession.startGameAndAwaitFirstTurn(): PlayerTurnInfo = coroutineScope {
    val gameStartedEvents = watchGameStarted()
    val deferredFirstTurn = async { gameStartedEvents.first() }
    startGame()
    deferredFirstTurn.await()
}

suspend fun SevenWondersSession.joinGameAndWaitFirstTurn(gameId: Long): PlayerTurnInfo = coroutineScope {
    val gameStartedEvents = watchGameStarted()
    val deferredFirstTurn = async { gameStartedEvents.first() }
    joinGame(gameId)
    deferredFirstTurn.await()
}

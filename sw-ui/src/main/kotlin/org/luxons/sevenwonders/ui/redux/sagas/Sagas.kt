package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.hildan.krossbow.stomp.ConnectionException
import org.hildan.krossbow.stomp.MissingHeartBeatException
import org.hildan.krossbow.stomp.WebSocketClosedUnexpectedly
import org.luxons.sevenwonders.client.*
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.router.Navigate
import org.luxons.sevenwonders.ui.router.Route
import org.luxons.sevenwonders.ui.router.routerSaga
import redux.RAction
import redux.WrapperAction
import webpack.isProdEnv

typealias SwSagaContext = SagaContext<SwState, RAction, WrapperAction>

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun SwSagaContext.rootSaga() = try {
    coroutineScope {
        val action = next<RequestChooseName>()
        val serverUrl = sevenWondersWebSocketUrl()
        console.info("Connecting to Seven Wonders web socket API...")
        val session = SevenWondersClient().connect(serverUrl)
        console.info("Connected!")

        launch(start = CoroutineStart.UNDISPATCHED) {
            serverErrorSaga(session)
        }

        launchApiActionHandlersIn(this, session)
        launchApiEventHandlersIn(this, session)

        val player = session.chooseNameAndAwait(action.playerName)
        dispatch(SetCurrentPlayerAction(player))

        routerSaga(Route.GAME_BROWSER) {
            when (it) {
                Route.HOME -> Unit
                Route.LOBBY -> lobbySaga(session)
                Route.GAME_BROWSER -> gameBrowserSaga(session)
                Route.GAME -> gameSaga(session)
            }
        }
    }
} catch (e: Exception) {
    console.error(e)
    dispatchFatalError(e)
}

private fun SwSagaContext.dispatchFatalError(throwable: Throwable) {
    when (throwable) {
        is ConnectionException -> dispatch(FatalError(throwable.message ?: "Couldn't connect to the server."))
        is MissingHeartBeatException -> dispatch(FatalError("The server doesn't seem to be responding."))
        is WebSocketClosedUnexpectedly -> dispatch(FatalError("The connection to the server was closed unexpectedly."))
        else -> dispatch(FatalError("An unexpected error occurred: ${throwable.message}"))
    }
}

private fun sevenWondersWebSocketUrl(): String {
    if (!isProdEnv()) {
        return "ws://localhost:8000"
    }
    // prevents mixed content requests
    val scheme = if (window.location.protocol.startsWith("https")) "wss" else "ws"
    return "$scheme://${window.location.host}"
}

private suspend fun serverErrorSaga(session: SevenWondersSession) {
    session.watchErrors().collect { err ->
        // These are not an error for the user, but rather for the programmer
        console.error("${err.code}: ${err.message}")
        console.error(JSON.stringify(err))
    }
}

private fun SwSagaContext.launchApiActionHandlersIn(scope: CoroutineScope, session: SevenWondersSession) {
    scope.launchOnEach<RequestChooseName> { session.chooseName(it.playerName) }

    scope.launchOnEach<RequestCreateGame> { session.createGame(it.gameName) }
    scope.launchOnEach<RequestJoinGame> { session.joinGame(it.gameId) }
    scope.launchOnEach<RequestLeaveLobby> { session.leaveLobby() }
    scope.launchOnEach<RequestDisbandLobby> { session.disbandLobby() }

    scope.launchOnEach<RequestAddBot> { session.addBot(it.botDisplayName) }
    scope.launchOnEach<RequestReorderPlayers> { session.reorderPlayers(it.orderedPlayers) }
    scope.launchOnEach<RequestReassignWonders> { session.reassignWonders(it.wonders) }
    scope.launchOnEach<RequestStartGame> { session.startGame() }

    scope.launchOnEach<RequestSayReady> { session.sayReady() }
    scope.launchOnEach<RequestPrepareMove> { session.prepareMove(it.move) }
    scope.launchOnEach<RequestUnprepareMove> { session.unprepareMove() }
    scope.launchOnEach<RequestLeaveGame> { session.leaveGame() }
}

private fun SwSagaContext.launchApiEventHandlersIn(scope: CoroutineScope, session: SevenWondersSession) {
    scope.launch {
        session.watchGameEvents().collect { event ->
            when (event) {
                is GameEvent.NameChosen -> {
                    dispatch(SetCurrentPlayerAction(event.player))
                    dispatch(Navigate(Route.GAME_BROWSER))
                }
                is GameEvent.LobbyJoined -> {
                    dispatch(EnterLobbyAction(event.lobby))
                    dispatch(Navigate(Route.LOBBY))
                }
                is GameEvent.LobbyUpdated -> {
                    dispatch(UpdateLobbyAction(event.lobby))
                }
                GameEvent.LobbyLeft -> {
                    dispatch(LeaveLobbyAction)
                    dispatch(Navigate(Route.GAME_BROWSER))
                }
                is GameEvent.GameStarted -> {
                    val currentLobby = reduxState.currentLobby ?: error("Received game started event without being in a lobby")
                    dispatch(EnterGameAction(currentLobby, event.turnInfo))
                    dispatch(Navigate(Route.GAME))
                }
                is GameEvent.NewTurnStarted -> dispatch(TurnInfoEvent(event.turnInfo))
                is GameEvent.MovePrepared -> dispatch(PreparedMoveEvent(event.move))
                is GameEvent.CardPrepared -> dispatch(PreparedCardEvent(event.preparedCard))
                is GameEvent.PlayerIsReady -> dispatch(PlayerReadyEvent(event.username))
                // Currently the move is already unprepared when launching the unprepare request
                // TODO add a "unpreparing" state and only update redux when the move is successfully unprepared
                GameEvent.MoveUnprepared -> {}
            }
        }
    }
}

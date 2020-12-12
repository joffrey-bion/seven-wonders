package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.browser.window
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.hildan.krossbow.stomp.ConnectionException
import org.hildan.krossbow.stomp.MissingHeartBeatException
import org.hildan.krossbow.stomp.WebSocketClosedUnexpectedly
import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.client.SevenWondersSession
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
        val session = SevenWondersClient().connect(serverUrl)
        console.info("Connected to Seven Wonders web socket API")

        launch(start = CoroutineStart.UNDISPATCHED) {
            serverErrorSaga(session)
        }

        launchApiActionHandlersIn(this, session)
        launchApiEventHandlersIn(this, session)

        val player = session.chooseName(action.playerName, null)
        dispatch(SetCurrentPlayerAction(player))

        routerSaga(Route.GAME_BROWSER) {
            when (it) {
                Route.HOME -> homeSaga(session)
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

    scope.launchOnEach<RequestCreateGame> { session.createGame(it.gameName) }
    scope.launchOnEach<RequestJoinGame> { session.joinGame(it.gameId) }
    scope.launchOnEach<RequestLeaveLobby> { session.leaveLobby() }
    scope.launchOnEach<RequestDisbandLobby> { session.disbandLobby() }

    scope.launchOnEach<RequestAddBot> { session.addBot(it.botDisplayName) }
    scope.launchOnEach<RequestReorderPlayers> { session.reorderPlayers(it.orderedPlayers) }
    scope.launchOnEach<RequestReassignWonders> { session.reassignWonders(it.wonders) }
    // mapAction<RequestUpdateSettings> { updateSettings(it.settings) }
    scope.launchOnEach<RequestStartGame> { session.startGame() }

    scope.launchOnEach<RequestSayReady> { session.sayReady() }
    scope.launchOnEach<RequestPrepareMove> { session.prepareMove(it.move) }
    scope.launchOnEach<RequestUnprepareMove> { session.unprepareMove() }
}

private fun SwSagaContext.launchApiEventHandlersIn(scope: CoroutineScope, session: SevenWondersSession) {

    scope.launch {
        session.watchLobbyJoined().collect { lobby ->
            dispatch(EnterLobbyAction(lobby))
            dispatch(Navigate(Route.LOBBY))
        }
    }

    scope.launch {
        session.watchLobbyLeft().collect { leftLobbyId ->
            dispatch(LeaveLobbyAction(leftLobbyId))
            dispatch(Navigate(Route.GAME_BROWSER))
        }
    }

    scope.launch {
        session.watchGameStarted().collect { turnInfo ->
            val currentLobby = getState().currentLobby ?: error("Received game started event without being in a lobby")
            dispatch(EnterGameAction(currentLobby, turnInfo))
            dispatch(Navigate(Route.GAME))
        }
    }

    // FIXME map this actions like others and await server event instead
    scope.launchOnEach<RequestLeaveGame> {
        session.leaveGame()
        dispatch(Navigate(Route.GAME_BROWSER))
    }
}

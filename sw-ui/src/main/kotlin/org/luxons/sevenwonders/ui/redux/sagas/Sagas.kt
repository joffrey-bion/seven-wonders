package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.RequestChooseName
import org.luxons.sevenwonders.ui.redux.SetCurrentPlayerAction
import org.luxons.sevenwonders.ui.redux.SwState
import org.luxons.sevenwonders.ui.router.Route
import org.luxons.sevenwonders.ui.router.routerSaga
import redux.RAction
import redux.WrapperAction
import webpack.isProdEnv
import kotlin.browser.window

typealias SwSagaContext = SagaContext<SwState, RAction, WrapperAction>

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun SwSagaContext.rootSaga() = coroutineScope {
    val action = next<RequestChooseName>()
    val port = if (isProdEnv()) window.location.port.ifEmpty { "80" } else "8000"
    val session = SevenWondersClient().connect("localhost:$port")
    console.info("Connected to Seven Wonders web socket API")

    launch(start = CoroutineStart.UNDISPATCHED) {
        serverErrorSaga(session)
    }

    val player = session.chooseName(action.playerName)
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

private suspend fun serverErrorSaga(session: SevenWondersSession) {
    session.watchErrors().collect { err ->
        // TODO use blueprintjs toaster
        console.error("${err.code}: ${err.message}")
        console.error(JSON.stringify(err))
    }
}

private suspend fun SwSagaContext.homeSaga(session: SevenWondersSession) {
    val action = next<RequestChooseName>()
    val player = session.chooseName(action.playerName)
    dispatch(SetCurrentPlayerAction(player))
}

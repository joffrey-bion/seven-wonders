package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
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

typealias SwSagaContext = SagaContext<SwState, RAction, WrapperAction>

@OptIn(ExperimentalCoroutinesApi::class)
suspend fun SwSagaContext.rootSaga() = coroutineScope {
    val action = next<RequestChooseName>()
    val session = SevenWondersClient().connect("localhost:8000")
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
    val errorsSub = session.watchErrors()
    for (err in errorsSub.messages) {
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

package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
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

suspend fun SwSagaContext.rootSaga() = coroutineScope {
    val action = next<RequestChooseName>()
    val session = SevenWondersClient().connect("localhost:8000")
    console.info("Connected to Seven Wonders web socket API")

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

private suspend fun SwSagaContext.homeSaga(session: SevenWondersSession): SevenWondersSession {
    val action = next<RequestChooseName>()
    val player = session.chooseName(action.playerName)
    dispatch(SetCurrentPlayerAction(player))
    return session
}

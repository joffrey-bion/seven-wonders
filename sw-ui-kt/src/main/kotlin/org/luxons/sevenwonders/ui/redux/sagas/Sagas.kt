package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.ui.redux.RequestChooseName
import org.luxons.sevenwonders.ui.redux.SetCurrentPlayerAction
import org.luxons.sevenwonders.ui.redux.SwState
import redux.RAction
import redux.WrapperAction

typealias SwSagaContext = SagaContext<SwState, RAction, WrapperAction>

suspend fun SwSagaContext.rootSaga() {
    val action = next<RequestChooseName>()
    val session = SevenWondersClient().connect("http://localhost:8000")

    coroutineScope {
        launch { gameBrowserSaga(session) }

        val player = session.chooseName(action.playerName)
        dispatch(SetCurrentPlayerAction(player))
        // push /games
    }
}

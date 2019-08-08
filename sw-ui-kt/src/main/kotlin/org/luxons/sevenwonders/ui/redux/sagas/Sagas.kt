package org.luxons.sevenwonders.ui.redux.sagas

import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.model.api.SEVEN_WONDERS_WS_ENDPOINT
import org.luxons.sevenwonders.ui.redux.RequestChooseName
import org.luxons.sevenwonders.ui.redux.SetCurrentPlayerAction
import org.luxons.sevenwonders.ui.redux.SwState
import redux.RAction
import redux.WrapperAction

fun rootSaga() = saga<SwState, RAction, WrapperAction> {
    val action = next<RequestChooseName>()
    val session = SevenWondersClient().connect(SEVEN_WONDERS_WS_ENDPOINT)

    fork(gameBrowserSaga(session))

    val player = session.chooseName(action.playerName)
    dispatch(SetCurrentPlayerAction(player))
    // push /games
}

package org.luxons.sevenwonders.ui.redux.sagas

import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.EnterGameAction
import org.luxons.sevenwonders.ui.redux.RequestStartGameAction
import org.luxons.sevenwonders.ui.redux.SwState
import org.luxons.sevenwonders.ui.redux.UpdateLobbyAction
import redux.RAction
import redux.WrapperAction

fun lobbySaga(session: SevenWondersSession, lobbyId: Long) = saga<SwState, RAction, WrapperAction> {
    fork(watchLobbyUpdates(session, lobbyId))
    fork(watchGameStart(session, lobbyId))
    fork(startGame(session))
}

private fun watchLobbyUpdates(session: SevenWondersSession, lobbyId: Long) = saga<SwState, RAction, WrapperAction> {
    val lobbyUpdates = session.watchLobbyUpdates(lobbyId)
    for (lobby in lobbyUpdates.messages) {
        dispatch(UpdateLobbyAction(lobby))
    }
}

private fun watchGameStart(session: SevenWondersSession, lobbyId: Long) = saga<SwState, RAction, WrapperAction> {
    val gameStartSubscription = session.watchGameStart(lobbyId)
    gameStartSubscription.messages.receive()
    dispatch(EnterGameAction(lobbyId))
    // TODO push /game/{lobby.id}
}

private fun startGame(session: SevenWondersSession) = actionHandlerSaga<SwState, RAction, WrapperAction, RequestStartGameAction> {
    session.startGame()
}

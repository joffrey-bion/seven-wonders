package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.EnterGameAction
import org.luxons.sevenwonders.ui.redux.RequestStartGameAction
import org.luxons.sevenwonders.ui.redux.UpdateLobbyAction
import org.luxons.sevenwonders.ui.router.Router

suspend fun SwSagaContext.lobbySaga(session: SevenWondersSession, lobbyId: Long) {
    coroutineScope {
        launch { watchLobbyUpdates(session, lobbyId) }
        launch { handleGameStart(session, lobbyId) }
        launch { watchStartGame(session) }
    }
}

private suspend fun SwSagaContext.watchLobbyUpdates(session: SevenWondersSession, lobbyId: Long) {
    val lobbyUpdates = session.watchLobbyUpdates(lobbyId)
    for (lobby in lobbyUpdates.messages) {
        dispatch(UpdateLobbyAction(lobby))
    }
}

private suspend fun SwSagaContext.handleGameStart(session: SevenWondersSession, lobbyId: Long) {
    val gameStartSubscription = session.watchGameStart(lobbyId)
    gameStartSubscription.messages.receive()
    dispatch(EnterGameAction(lobbyId))

    coroutineScope {
        launch { gameSaga(session, lobbyId) }
        Router.game(lobbyId)
    }
}

private suspend fun SwSagaContext.watchStartGame(session: SevenWondersSession) = onEach<RequestStartGameAction> {
    session.startGame()
}

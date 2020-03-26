package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompSubscription
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.ui.redux.EnterGameAction
import org.luxons.sevenwonders.ui.redux.RequestStartGameAction
import org.luxons.sevenwonders.ui.redux.UpdateLobbyAction
import org.luxons.sevenwonders.ui.router.Navigate
import org.luxons.sevenwonders.ui.router.Route

suspend fun SwSagaContext.lobbySaga(session: SevenWondersSession) {
    val lobbyId = getState().currentLobbyId ?: error("Lobby saga run without a current lobby")
    coroutineScope {
        val lobbyUpdatesSubscription = session.watchLobbyUpdates(lobbyId)
        launch { watchLobbyUpdates(lobbyUpdatesSubscription) }
        val startGameJob = launch { awaitStartGame(session) }

        awaitGameStart(session, lobbyId)

        lobbyUpdatesSubscription.unsubscribe()
        startGameJob.cancel()
        dispatch(Navigate(Route.GAME))
    }
}

private suspend fun SwSagaContext.watchLobbyUpdates(lobbyUpdatesSubscription: StompSubscription<LobbyDTO>) {
    dispatchAll(lobbyUpdatesSubscription.messages) { UpdateLobbyAction(it) }
}

private suspend fun SwSagaContext.awaitGameStart(session: SevenWondersSession, lobbyId: Long) {
    session.awaitGameStart(lobbyId)
    dispatch(EnterGameAction(lobbyId))
}

private suspend fun SwSagaContext.awaitStartGame(session: SevenWondersSession) {
    next<RequestStartGameAction>()
    session.startGame()
}

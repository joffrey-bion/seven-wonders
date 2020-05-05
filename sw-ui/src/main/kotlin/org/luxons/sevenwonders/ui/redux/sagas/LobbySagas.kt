package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.EnterGameAction
import org.luxons.sevenwonders.ui.redux.RequestLeaveLobby
import org.luxons.sevenwonders.ui.redux.RequestStartGame
import org.luxons.sevenwonders.ui.redux.UpdateLobbyAction
import org.luxons.sevenwonders.ui.router.Navigate
import org.luxons.sevenwonders.ui.router.Route
import org.luxons.sevenwonders.ui.utils.awaitFirst

suspend fun SwSagaContext.lobbySaga(session: SevenWondersSession) {
    val lobby = getState().currentLobby ?: error("Lobby saga run without a current lobby")
    coroutineScope {
        val lobbyUpdatesSubscription = session.watchLobbyUpdates()
            .map { UpdateLobbyAction(it) }
            .dispatchAllIn(this)

        val startGameJob = launch { awaitStartGame(session) }

        awaitFirst(
            {
                awaitLeaveLobby(session)
                lobbyUpdatesSubscription.cancel()
                startGameJob.cancel()
                dispatch(Navigate(Route.GAME_BROWSER))
            },
            {
                awaitGameStart(session, lobby.id)
                lobbyUpdatesSubscription.cancel()
                startGameJob.cancel()
                dispatch(Navigate(Route.GAME))
            }
        )
    }
}

private suspend fun SwSagaContext.awaitGameStart(session: SevenWondersSession, lobbyId: Long) {
    val turnInfo = session.awaitGameStart(lobbyId)
    val lobby = getState().currentLobby!!
    dispatch(EnterGameAction(lobby, turnInfo))
}

private suspend fun SwSagaContext.awaitStartGame(session: SevenWondersSession) {
    next<RequestStartGame>()
    session.startGame()
}

private suspend fun SwSagaContext.awaitLeaveLobby(session: SevenWondersSession) {
    next<RequestLeaveLobby>()
    session.leaveLobby()
}

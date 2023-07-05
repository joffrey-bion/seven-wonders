package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.flow.map
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.router.Navigate
import org.luxons.sevenwonders.ui.router.SwRoute

suspend fun SwSagaContext.gameBrowserSaga(session: SevenWondersSession) {
    // browser navigation could have brought us here: we should leave the game/lobby
    ensureNoCurrentGameNorLobby(session)
    session.watchGames()
        .map { UpdateGameListAction(it) }
        .collect { dispatch(it) }
}

private suspend fun SwSagaContext.ensureNoCurrentGameNorLobby(session: SevenWondersSession) {
    if (reduxState.gameState != null) {
        console.warn("User left a game via browser navigation, telling the server...")
        session.leaveGame()
    } else if (reduxState.currentLobby != null) {
        console.warn("User left the lobby via browser navigation, telling the server...")
        session.leaveLobby()
    }
}

suspend fun SwSagaContext.lobbySaga(session: SevenWondersSession) {
    if (reduxState.gameState != null) {
        console.warn("User left a game via browser navigation, telling the server...")
        session.leaveGame()
    } else if (reduxState.currentLobby == null) {
        console.warn("User went to lobby page via browser navigation, redirecting to game browser...")
        dispatch(Navigate(SwRoute.GAME_BROWSER))
    }
}

suspend fun SwSagaContext.gameSaga(session: SevenWondersSession) {
    if (reduxState.gameState == null) {
        // TODO properly redirect somewhere
        error("Game saga run without a current game")
    }
    // notifies the server that the client is ready to receive the first hand
    session.sayReady()
}

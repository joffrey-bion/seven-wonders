package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.router.Navigate
import org.luxons.sevenwonders.ui.router.Route

suspend fun SwSagaContext.homeSaga(session: SevenWondersSession) {
    val action = next<RequestChooseName>()
    val player = session.chooseName(action.playerName)
    dispatch(SetCurrentPlayerAction(player))
    dispatch(Navigate(Route.GAME_BROWSER))
}

suspend fun SwSagaContext.gameBrowserSaga(session: SevenWondersSession) {
    // browser navigation could have brought us here: we should leave the game/lobby
    ensureNoCurrentGameNorLobby(session)
    session.watchGames().map { UpdateGameListAction(it) }.dispatchAll()
}

private suspend fun SwSagaContext.ensureNoCurrentGameNorLobby(session: SevenWondersSession) {
    if (reduxState.gameState != null) {
        console.warn("User left a game via browser navigation, cleaning up...")
        session.leaveGame()
    } else if (reduxState.currentLobby != null) {
        console.warn("User left the lobby via browser navigation, cleaning up...")
        session.leaveLobby()
    }
}

suspend fun SwSagaContext.lobbySaga(session: SevenWondersSession) {
    // browser navigation could have brought us here: we should leave the current game in that case
    if (reduxState.gameState != null) {
        console.warn("User left a game via browser navigation, telling the server...")
        session.leaveGame()
        return
    }
    // browser navigation could have brought us here: we should go back to game browser if no lobby
    if (reduxState.currentLobby == null) {
        console.warn("User went to lobby via browser navigation, cleaning up...")
        dispatch(Navigate(Route.GAME_BROWSER))
        return
    }
    session.watchLobbyUpdates().map { UpdateLobbyAction(it) }.dispatchAll()
}

suspend fun SwSagaContext.gameSaga(session: SevenWondersSession) {
    val game = reduxState.gameState ?: error("Game saga run without a current game")
    coroutineScope {
        session.watchGameEvents(game.gameId).map {
            when (it) {
                is GameEvent.NewTurnStarted -> TurnInfoEvent(it.turnInfo)
                is GameEvent.MovePrepared -> PreparedMoveEvent(it.move)
                is GameEvent.CardPrepared -> PreparedCardEvent(it.preparedCard)
                is GameEvent.PlayerIsReady -> PlayerReadyEvent(it.username)
            }
        }.dispatchAllIn(this)
        session.sayReady()
    }
    console.log("End of game saga")
}

package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import org.luxons.sevenwonders.client.SevenWondersSession
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
    session.watchGames().map { UpdateGameListAction(it) }.dispatchAll()
}

suspend fun SwSagaContext.lobbySaga(session: SevenWondersSession) {
    val lobby = getState().currentLobby ?: error("Lobby saga run without a current lobby")
    coroutineScope {
        session.watchLobbyUpdates().map { UpdateLobbyAction(it) }.dispatchAllIn(this)

        val turnInfo = session.awaitGameStart(lobby.id)
        dispatch(EnterGameAction(getState().currentLobby!!, turnInfo))
        dispatch(Navigate(Route.GAME))
    }
}

suspend fun SwSagaContext.gameSaga(session: SevenWondersSession) {
    val game = getState().gameState ?: error("Game saga run without a current game")
    coroutineScope {
        session.watchPlayerReady(game.id).map { PlayerReadyEvent(it) }.dispatchAllIn(this)
        session.watchPreparedCards(game.id).map { PreparedCardEvent(it) }.dispatchAllIn(this)
        session.watchOwnMoves().map { PreparedMoveEvent(it) }.dispatchAllIn(this)
        session.watchTurns().map { TurnInfoEvent(it) }.dispatchAllIn(this)
    }
    console.log("End of game saga")
}

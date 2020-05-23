package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.router.Navigate
import org.luxons.sevenwonders.ui.router.Route

suspend fun SwSagaContext.gameSaga(session: SevenWondersSession) {
    val game = getState().gameState ?: error("Game saga run without a current game")
    coroutineScope {
        session.watchPlayerReady(game.id).map { PlayerReadyEvent(it) }.dispatchAllIn(this)
        session.watchPreparedCards(game.id).map { PreparedCardEvent(it) }.dispatchAllIn(this)
        session.watchOwnMoves().map { PreparedMoveEvent(it) }.dispatchAllIn(this)
        session.watchTurns().map { TurnInfoEvent(it) }.dispatchAllIn(this)

        launch { onEach<RequestSayReady> { session.sayReady() } }
        launch { onEach<RequestPrepareMove> { session.prepareMove(it.move) } }
        launch { onEach<RequestUnprepareMove> { session.unprepareMove() } }

        next<RequestLeaveGame>()
        session.leaveGame()
        dispatch(Navigate(Route.GAME_BROWSER))
    }
    console.log("End of game saga")
}

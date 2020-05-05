package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.PlayerReadyEvent
import org.luxons.sevenwonders.ui.redux.PreparedCardEvent
import org.luxons.sevenwonders.ui.redux.PreparedMoveEvent
import org.luxons.sevenwonders.ui.redux.RequestPrepareMove
import org.luxons.sevenwonders.ui.redux.RequestSayReady
import org.luxons.sevenwonders.ui.redux.RequestUnprepareMove
import org.luxons.sevenwonders.ui.redux.TurnInfoEvent

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

        // TODO await game end and cancel this scope to unsubscribe everything
    }
    console.log("End of game saga")
}

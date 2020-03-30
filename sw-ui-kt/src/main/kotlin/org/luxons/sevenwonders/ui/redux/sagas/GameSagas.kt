package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.PlayerReadyEvent
import org.luxons.sevenwonders.ui.redux.PreparedCardEvent
import org.luxons.sevenwonders.ui.redux.RequestPrepareMove
import org.luxons.sevenwonders.ui.redux.RequestSayReady
import org.luxons.sevenwonders.ui.redux.TurnInfoEvent

suspend fun SwSagaContext.gameSaga(session: SevenWondersSession) {
    val game = getState().gameState ?: error("Game saga run without a current game")
    coroutineScope {
        val playerReadySub = session.watchPlayerReady(game.id)
        val preparedCardsSub = session.watchPreparedCards(game.id)
        val turnInfoSub = session.watchTurns()
        val sayReadyJob = launch { onEach<RequestSayReady> { session.sayReady() } }
        val prepareMoveJob = launch { onEach<RequestPrepareMove> { session.prepareMove(it.move) } }
        launch { dispatchAll(playerReadySub.messages) { PlayerReadyEvent(it) } }
        launch { dispatchAll(preparedCardsSub.messages) { PreparedCardEvent(it) } }
        launch { dispatchAll(turnInfoSub.messages) { TurnInfoEvent(it) } }
        // TODO await game end
        // TODO unsubscribe all subs, cancel all jobs
    }
    console.log("End of game saga")
}


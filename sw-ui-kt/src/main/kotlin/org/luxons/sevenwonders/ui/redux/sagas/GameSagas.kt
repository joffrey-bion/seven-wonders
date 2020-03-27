package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.model.api.State
import org.luxons.sevenwonders.ui.redux.PlayerReadyEvent
import org.luxons.sevenwonders.ui.redux.PreparedCardEvent
import org.luxons.sevenwonders.ui.redux.RequestPrepareMove
import org.luxons.sevenwonders.ui.redux.RequestSayReady
import org.luxons.sevenwonders.ui.redux.TableUpdateEvent
import org.luxons.sevenwonders.ui.redux.TurnInfoEvent

suspend fun SwSagaContext.gameSaga(session: SevenWondersSession) {
    val lobby = getState().currentLobby ?: error("Game saga run without a current game")
    if (lobby.state != State.PLAYING) {
        error("Game saga run but the game hasn't started")
    }
    coroutineScope {
        val playerReadySub = session.watchPlayerReady(lobby.id)
        val preparedCardsSub = session.watchPreparedCards(lobby.id)
        val tableUpdatesSub = session.watchTableUpdates(lobby.id)
        val turnInfoSub = session.watchTurns()
        val sayReadyJob = launch { onEach<RequestSayReady> { session.sayReady() } }
        val prepareMoveJob = launch { onEach<RequestPrepareMove> { session.prepareMove(it.move) } }
        launch { dispatchAll(playerReadySub.messages) { PlayerReadyEvent(it) } }
        launch { dispatchAll(preparedCardsSub.messages) { PreparedCardEvent(it) } }
        launch { dispatchAll(tableUpdatesSub.messages) { TableUpdateEvent(it) } }
        launch { dispatchAll(turnInfoSub.messages) { TurnInfoEvent(it) } }
        // TODO await game end
        // TODO unsubscribe all subs, cancel all jobs
    }
}


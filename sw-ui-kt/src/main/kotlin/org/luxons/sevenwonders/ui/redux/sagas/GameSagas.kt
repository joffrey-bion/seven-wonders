package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.ui.redux.SwState
import redux.RAction
import redux.WrapperAction

suspend fun SwSagaContext.gameSaga(session: SevenWondersSession, gameId: Long) {
    coroutineScope {
        launch { watchPlayerReady(session, gameId) }
    }
}

private suspend fun SwSagaContext.watchPlayerReady(session: SevenWondersSession, gameId: Long) {
    session.watchPlayerReady(gameId)
}


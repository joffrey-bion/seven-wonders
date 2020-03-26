package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.model.api.State

suspend fun SwSagaContext.gameSaga(session: SevenWondersSession) {
    val lobby = getState().currentLobby ?: error("Game saga run without a current game")
    if (lobby.state != State.PLAYING) {
        error("Game saga run but the game hasn't started")
    }
    coroutineScope {
        launch { watchPlayerReady(session, lobby.id) }
    }
}

private suspend fun SwSagaContext.watchPlayerReady(session: SevenWondersSession, gameId: Long) {
    session.watchPlayerReady(gameId)
}


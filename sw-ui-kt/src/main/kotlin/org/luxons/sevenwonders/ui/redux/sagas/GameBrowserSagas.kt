package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.ui.redux.EnterLobbyAction
import org.luxons.sevenwonders.ui.redux.RequestCreateGameAction
import org.luxons.sevenwonders.ui.redux.RequestJoinGameAction
import org.luxons.sevenwonders.ui.redux.UpdateGameListAction
import org.luxons.sevenwonders.ui.redux.UpdateLobbyAction
import org.luxons.sevenwonders.ui.router.Router
import kotlin.coroutines.coroutineContext

suspend fun SwSagaContext.gameBrowserSaga(session: SevenWondersSession) {
    coroutineScope {
        launch { watchGames(session) }
        launch { watchCreateGame(session) }
        launch { watchJoinGame(session) }
    }
}

private suspend fun SwSagaContext.watchGames(session: SevenWondersSession) {
    val gamesSubscription = session.watchGames()
    for (lobbies in gamesSubscription.messages) {
        if (!coroutineContext.isActive) {
            gamesSubscription.unsubscribe()
            break
        }
        dispatch(UpdateGameListAction(lobbies.toList()))
    }
}

private suspend fun SwSagaContext.watchCreateGame(session: SevenWondersSession) =
        onEach<RequestCreateGameAction> {
            val lobby = session.createGame(it.gameName)
            handleGameJoined(session, lobby)
        }

private suspend fun SwSagaContext.watchJoinGame(session: SevenWondersSession) =
        onEach<RequestJoinGameAction> {
            val lobby = session.joinGame(it.gameId)
            handleGameJoined(session, lobby)
        }

private suspend fun SwSagaContext.handleGameJoined(
    session: SevenWondersSession,
    lobby: LobbyDTO
) {
    dispatch(UpdateLobbyAction(lobby))
    dispatch(EnterLobbyAction(lobby.id))
    coroutineScope {
        launch { lobbySaga(session, lobby.id) }
        Router.lobby(lobby.id)
    }
}

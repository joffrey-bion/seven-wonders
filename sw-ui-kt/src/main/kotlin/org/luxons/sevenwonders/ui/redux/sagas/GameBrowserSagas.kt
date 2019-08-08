package org.luxons.sevenwonders.ui.redux.sagas

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.ui.redux.EnterLobbyAction
import org.luxons.sevenwonders.ui.redux.RequestCreateGameAction
import org.luxons.sevenwonders.ui.redux.RequestJoinGameAction
import org.luxons.sevenwonders.ui.redux.SwState
import org.luxons.sevenwonders.ui.redux.UpdateGameListAction
import org.luxons.sevenwonders.ui.redux.UpdateLobbyAction
import redux.RAction
import redux.WrapperAction

fun gameBrowserSaga(session: SevenWondersSession) = saga<SwState, RAction, WrapperAction> {
    val watchGamesJob = fork(watchGames(session))
    val watchCreateGameJob = fork(watchCreateGame(session))
    val watchJoinGameJob = fork(watchJoinGame(session))
}

private fun watchGames(session: SevenWondersSession) = saga<SwState, RAction, WrapperAction> {
    val gamesSubscription = session.watchGames()
    for (lobbies in gamesSubscription.messages) {
        if (!isActive) {
            gamesSubscription.unsubscribe()
            break
        }
        dispatch(UpdateGameListAction(lobbies.toList()))
    }
}

private fun watchCreateGame(session: SevenWondersSession) =
        actionHandlerSaga<SwState, RAction, WrapperAction, RequestCreateGameAction> {
            val lobby = session.createGame(it.gameName)
            handleGameJoined(session, lobby)
        }

private fun watchJoinGame(session: SevenWondersSession) = actionHandlerSaga<SwState, RAction, WrapperAction, RequestJoinGameAction> {
    val lobby = session.joinGame(it.gameId)
    handleGameJoined(session, lobby)
}

private fun SagaContext<SwState, RAction, WrapperAction>.handleGameJoined(
    session: SevenWondersSession,
    lobby: LobbyDTO
) {
    dispatch(UpdateLobbyAction(lobby))
    dispatch(EnterLobbyAction(lobby.id))
    fork(lobbySaga(session, lobby.id))
    // TODO push /lobby/{lobby.id}
}

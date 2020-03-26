package org.luxons.sevenwonders.ui.redux.sagas

import com.palantir.blueprintjs.org.luxons.sevenwonders.ui.utils.awaitFirst
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.hildan.krossbow.stomp.StompSubscription
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.ui.redux.EnterLobbyAction
import org.luxons.sevenwonders.ui.redux.RequestCreateGame
import org.luxons.sevenwonders.ui.redux.RequestJoinGame
import org.luxons.sevenwonders.ui.redux.UpdateGameListAction
import org.luxons.sevenwonders.ui.router.Navigate
import org.luxons.sevenwonders.ui.router.Route

suspend fun SwSagaContext.gameBrowserSaga(session: SevenWondersSession) {
    GameBrowserSaga(session, this).run()
}

private class GameBrowserSaga(
    private val session: SevenWondersSession,
    private val sagaContext: SwSagaContext
) {
    suspend fun run() {
        coroutineScope {
            val gamesSubscription = session.watchGames()
            launch { dispatchGameUpdates(gamesSubscription) }
            val lobby = awaitCreateOrJoinGame()
            gamesSubscription.unsubscribe()
            sagaContext.dispatch(EnterLobbyAction(lobby))
            sagaContext.dispatch(Navigate(Route.LOBBY))
        }
    }

    private suspend fun dispatchGameUpdates(gamesSubscription: StompSubscription<List<LobbyDTO>>) {
        sagaContext.dispatchAll(gamesSubscription.messages) { UpdateGameListAction(it) }
    }

    private suspend fun awaitCreateOrJoinGame(): LobbyDTO = awaitFirst(this::awaitCreateGame, this::awaitJoinGame)

    private suspend fun awaitCreateGame(): LobbyDTO {
        val action = sagaContext.next<RequestCreateGame>()
        return session.createGame(action.gameName)
    }

    private suspend fun awaitJoinGame(): LobbyDTO {
        val action = sagaContext.next<RequestJoinGame>()
        return session.joinGame(action.gameId)
    }
}

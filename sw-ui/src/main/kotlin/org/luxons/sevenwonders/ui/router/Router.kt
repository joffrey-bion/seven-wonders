package org.luxons.sevenwonders.ui.router

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.ui.redux.sagas.SwSagaContext
import redux.RAction
import kotlin.browser.window

enum class Route(val path: String) {
    HOME("/"),
    GAME_BROWSER("/games"),
    LOBBY("/lobby"),
    GAME("/game"),
}

data class Navigate(val route: Route): RAction

suspend fun SwSagaContext.routerSaga(
    startRoute: Route,
    runRouteSaga: suspend SwSagaContext.(Route) -> Unit
) {
    coroutineScope {
        window.location.hash = startRoute.path
        var currentSaga: Job = launch { runRouteSaga(startRoute) }
        onEach<Navigate> {
            currentSaga.cancel()
            window.location.hash = it.route.path
            currentSaga = launch { runRouteSaga(it.route) }
        }
    }
}

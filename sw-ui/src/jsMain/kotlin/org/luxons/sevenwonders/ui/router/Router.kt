package org.luxons.sevenwonders.ui.router

import kotlinx.browser.window
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.luxons.sevenwonders.ui.redux.sagas.SwSagaContext
import redux.RAction

enum class SwRoute(val path: String) {
    HOME("/"),
    GAME_BROWSER("/games"),
    LOBBY("/lobby"),
    GAME("/game");

    companion object {
        private val all = entries.associateBy { it.path }

        fun from(path: String) = all.getValue(path)
    }
}

data class Navigate(val route: SwRoute) : RAction

suspend fun SwSagaContext.routerSaga(
    startRoute: SwRoute,
    runRouteSaga: suspend SwSagaContext.(SwRoute) -> Unit,
) {
    coroutineScope {
        window.location.hash = startRoute.path
        launch { changeRouteOnNavigateAction() }
        var currentSaga: Job = launch { runRouteSaga(startRoute) }
        window.onhashchange = { event ->
            val route = SwRoute.from(event.newURL.substringAfter("#"))
            currentSaga.cancel()
            currentSaga = this@coroutineScope.launch {
                runRouteSaga(route)
            }
            Unit
        }
    }
}

suspend fun SwSagaContext.changeRouteOnNavigateAction() {
    onEach<Navigate> {
        window.location.hash = it.route.path
    }
}

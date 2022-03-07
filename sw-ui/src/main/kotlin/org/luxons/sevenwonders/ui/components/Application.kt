package org.luxons.sevenwonders.ui.components

import org.luxons.sevenwonders.ui.components.errors.errorDialog
import org.luxons.sevenwonders.ui.components.game.gameScene
import org.luxons.sevenwonders.ui.components.gameBrowser.gameBrowser
import org.luxons.sevenwonders.ui.components.home.home
import org.luxons.sevenwonders.ui.components.lobby.lobby
import org.luxons.sevenwonders.ui.router.SwRoute
import react.RBuilder
import react.router.dom.*

fun RBuilder.application() = HashRouter {
    errorDialog()
    Switch {
        route(SwRoute.GAME_BROWSER.path) { gameBrowser() }
        route(SwRoute.GAME.path) { gameScene() }
        route(SwRoute.LOBBY.path) { lobby() }
        route(SwRoute.HOME.path) { home() }
        Redirect {
            attrs {
                from = "*"
                to = "/"
            }
        }
    }
}

private fun RBuilder.route(path: String, exact: Boolean = false, render: RBuilder.() -> Unit) {
    Route {
        attrs.path = arrayOf(path)
        attrs.exact = exact
        render()
    }
}

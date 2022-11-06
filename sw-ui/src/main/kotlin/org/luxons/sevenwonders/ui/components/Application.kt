package org.luxons.sevenwonders.ui.components

import org.luxons.sevenwonders.ui.components.errors.errorDialog
import org.luxons.sevenwonders.ui.components.game.gameScene
import org.luxons.sevenwonders.ui.components.gameBrowser.gameBrowser
import org.luxons.sevenwonders.ui.components.home.home
import org.luxons.sevenwonders.ui.components.lobby.lobby
import org.luxons.sevenwonders.ui.router.SwRoute
import react.Props
import react.RBuilder
import react.RElementBuilder
import react.createElement
import react.router.Navigate
import react.router.Route
import react.router.Routes
import react.router.RoutesProps
import react.router.dom.*

fun RBuilder.application() = HashRouter {
    errorDialog()
    Routes {
        route(SwRoute.GAME_BROWSER.path) { gameBrowser() }
        route(SwRoute.GAME.path) { gameScene() }
        route(SwRoute.LOBBY.path) { lobby() }
        route(SwRoute.HOME.path) { home() }
        route("*") {
            Navigate {
                attrs.to = "/"
                attrs.replace = true
            }
        }
    }
}

private fun RElementBuilder<RoutesProps>.route(path: String, render: RBuilder.() -> Unit) {
    Route {
        attrs.path = path
        attrs.element = createElement<Props>(render)
    }
}

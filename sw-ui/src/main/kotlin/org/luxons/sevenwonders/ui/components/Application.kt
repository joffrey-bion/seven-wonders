package org.luxons.sevenwonders.ui.components

import org.luxons.sevenwonders.ui.components.errors.errorDialog
import org.luxons.sevenwonders.ui.components.game.gameScene
import org.luxons.sevenwonders.ui.components.gameBrowser.gameBrowser
import org.luxons.sevenwonders.ui.components.home.home
import org.luxons.sevenwonders.ui.components.lobby.lobby
import org.luxons.sevenwonders.ui.router.Route
import react.RBuilder
import react.router.dom.hashRouter
import react.router.dom.redirect
import react.router.dom.route
import react.router.dom.switch

fun RBuilder.application() = hashRouter {
    errorDialog()
    switch {
        route(Route.GAME_BROWSER.path) { gameBrowser() }
        route(Route.GAME.path) { gameScene() }
        route(Route.LOBBY.path) { lobby() }
        route(Route.HOME.path, exact = true) { home() }
        redirect(from = "*", to = "/")
    }
}

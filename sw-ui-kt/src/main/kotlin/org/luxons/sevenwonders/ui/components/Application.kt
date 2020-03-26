package org.luxons.sevenwonders.ui.components

import org.luxons.sevenwonders.ui.components.game.gameScene
import org.luxons.sevenwonders.ui.components.gameBrowser.gameBrowser
import react.RBuilder
import react.router.dom.hashRouter
import react.router.dom.redirect
import react.router.dom.route
import react.router.dom.switch

import org.luxons.sevenwonders.ui.components.home.home
import org.luxons.sevenwonders.ui.components.lobby.lobby
import react.RProps

interface IdProps : RProps {
    val id: Long
}

fun RBuilder.application() = hashRouter {
    switch {
        route("/games") { gameBrowser() }
        route("/game") { gameScene() }
        route("/lobby") { lobby() }
        route("/", exact = true) { home() }
        redirect(from = "*", to = "/")
    }
}

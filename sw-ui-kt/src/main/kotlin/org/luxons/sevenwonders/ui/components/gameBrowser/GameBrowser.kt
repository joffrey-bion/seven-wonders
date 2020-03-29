package org.luxons.sevenwonders.ui.components.gameBrowser

import react.RBuilder
import react.dom.*

fun RBuilder.gameBrowser() = div {
    h1 { +"Games" }
    createGameForm {}
    gameList()
}

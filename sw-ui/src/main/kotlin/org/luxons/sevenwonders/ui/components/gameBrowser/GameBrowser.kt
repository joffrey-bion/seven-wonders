package org.luxons.sevenwonders.ui.components.gameBrowser

import kotlinx.css.*
import react.RBuilder
import react.dom.*
import styled.css
import styled.styledDiv

fun RBuilder.gameBrowser() = styledDiv {
    css {
        margin(all = 1.rem)
    }
    h1 { +"Games" }
    createGameForm {}
    gameList()
}

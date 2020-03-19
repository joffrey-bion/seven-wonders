package org.luxons.sevenwonders.ui.components.game

import react.RBuilder
import react.dom.*

fun RBuilder.gameScene(gameId: Long) = div {
    h1 { +"Game $gameId" }
}

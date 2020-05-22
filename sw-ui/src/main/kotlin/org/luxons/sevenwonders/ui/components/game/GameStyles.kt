package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import styled.StyleSheet

object GameStyles : StyleSheet("GameStyles", isStatic = true) {

    val scoreBoard by css {
        backgroundColor = Color.paleGoldenrod
    }
}

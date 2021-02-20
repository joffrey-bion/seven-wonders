package org.luxons.sevenwonders.ui.components.gameBrowser

import kotlinx.css.*
import styled.StyleSheet

object GameBrowserStyles : StyleSheet("GameBrowserStyles", isStatic = true) {

    val cardTitle by css {
        marginTop = 0.px
    }

    val createGameCard by css {
        marginBottom = 1.rem
    }

    val gameTable by css {
        width = 100.pct
    }
}

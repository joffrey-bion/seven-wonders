package org.luxons.sevenwonders.ui.components.lobby

import kotlinx.css.*
import styled.StyleSheet

object LobbyStyles : StyleSheet("LobbyStyles", isStatic = true) {

    val setupPanel by css {
        position = Position.fixed
        top = 2.rem
        right = 1.rem
        width = 15.rem
    }

    val wonderTagSideA by css {
        backgroundColor = Color.seaGreen
    }

    val wonderTagSideB by css {
        backgroundColor = Color.darkRed
    }
}

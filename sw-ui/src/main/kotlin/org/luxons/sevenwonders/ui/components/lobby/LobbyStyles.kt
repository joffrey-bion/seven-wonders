package org.luxons.sevenwonders.ui.components.lobby

import kotlinx.css.*
import org.luxons.sevenwonders.ui.components.GlobalStyles
import styled.StyleSheet

object LobbyStyles : StyleSheet("LobbyStyles", isStatic = true) {

    val contentContainer by css {
        margin(horizontal = LinearDimension.auto)
        maxWidth = GlobalStyles.preGameWidth
    }

    val setupPanel by css {
        position = Position.fixed
        top = 2.rem
        right = 1.rem
        width = 20.rem
    }
}

package org.luxons.sevenwonders.ui.components.home

import kotlinx.css.*
import styled.StyleSheet

object HomeStyles : StyleSheet("HomeStyles", isStatic = true) {

    val homeRoot by css {
        background = "url('images/background-zeus-temple.jpg') center no-repeat"
        backgroundSize = "cover"
    }

    val center by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = Align.center
        justifyContent = JustifyContent.center
    }

    val fullscreen by css {
        position = Position.fixed
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
        overflow = Overflow.hidden
    }
}

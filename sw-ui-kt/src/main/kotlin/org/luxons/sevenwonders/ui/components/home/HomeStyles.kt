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
        top = LinearDimension("0")
        left = LinearDimension("0")
        bottom = LinearDimension("0")
        right = LinearDimension("0")
        overflow = Overflow.hidden
    }
}

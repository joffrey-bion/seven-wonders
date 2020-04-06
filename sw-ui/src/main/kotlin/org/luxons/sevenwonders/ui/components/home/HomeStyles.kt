package org.luxons.sevenwonders.ui.components.home

import kotlinx.css.*
import styled.StyleSheet

object HomeStyles : StyleSheet("HomeStyles", isStatic = true) {

    val zeusBackground by css {
        background = "url('images/background-zeus-temple.jpg') center no-repeat"
        backgroundSize = "cover"
    }

    val centerChildren by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = Align.center
        justifyContent = JustifyContent.center
    }
}

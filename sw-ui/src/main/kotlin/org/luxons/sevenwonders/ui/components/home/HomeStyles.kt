package org.luxons.sevenwonders.ui.components.home

import kotlinx.css.*
import styled.StyleSheet

object HomeStyles : StyleSheet("HomeStyles", isStatic = true) {

    val centerChildren by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = Align.center
        justifyContent = JustifyContent.center
    }
}

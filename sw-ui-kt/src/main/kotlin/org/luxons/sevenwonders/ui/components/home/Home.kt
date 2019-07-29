package org.luxons.sevenwonders.ui.components.home

import react.RBuilder
import react.dom.*
import styled.css
import styled.styledDiv

private const val LOGO = "images/logo-7-wonders.png"

fun RBuilder.home() = styledDiv {
    css {
        +HomeStyles.fullscreen
        +HomeStyles.center
        +HomeStyles.homeRoot
    }

    img(src = LOGO, alt = "Seven Wonders") {}

    p {
        +"Great app!"
    }
}

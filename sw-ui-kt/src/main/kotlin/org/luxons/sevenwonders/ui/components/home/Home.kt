package org.luxons.sevenwonders.ui.components.home

import org.luxons.sevenwonders.ui.components.GlobalStyles
import react.RBuilder
import react.dom.*
import styled.css
import styled.styledDiv

private const val LOGO = "images/logo-7-wonders.png"

fun RBuilder.home() = styledDiv {
    css {
        +GlobalStyles.fullscreen
        +HomeStyles.centerChildren
        +HomeStyles.zeusBackground
    }

    img(src = LOGO, alt = "Seven Wonders") {}

    chooseNameForm {}
}

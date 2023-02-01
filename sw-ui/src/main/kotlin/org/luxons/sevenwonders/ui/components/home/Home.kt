package org.luxons.sevenwonders.ui.components.home

import emotion.react.*
import org.luxons.sevenwonders.ui.components.*
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img

private const val LOGO = "images/logo-7-wonders.png"

val Home = VFC("Home") {
    div {
        css(GlobalStyles.fullscreen, GlobalStyles.zeusBackground, HomeStyles.centerChildren) {}

        img {
            src = LOGO
            alt = "Seven Wonders"
        }

        ChooseNameForm()
    }
}

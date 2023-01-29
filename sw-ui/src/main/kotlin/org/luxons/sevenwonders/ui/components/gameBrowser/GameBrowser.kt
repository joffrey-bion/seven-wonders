package org.luxons.sevenwonders.ui.components.gameBrowser

import blueprintjs.core.*
import kotlinx.css.*
import kotlinx.html.*
import org.luxons.sevenwonders.ui.components.GlobalStyles
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.*
import styled.*

fun RBuilder.gameBrowser() = styledDiv {
    css {
        +GlobalStyles.fullscreen
        +GlobalStyles.zeusBackground
        padding(all = 1.rem)
    }
    styledDiv {
        attrs {
            classes += Classes.DARK
        }
        css {
            margin(horizontal = LinearDimension.auto)
            maxWidth = GlobalStyles.preGameWidth
        }
        styledDiv {
            css {
                display = Display.flex
                justifyContent = JustifyContent.spaceBetween
            }
            h1 { +"Games" }
            currentPlayerInfo()
        }

        bpCard(className = GameBrowserStyles.getTypedClassName { it::createGameCard }) {
            styledH2 {
                css { +GameBrowserStyles.cardTitle }
                +"Create a Game"
            }
            createGameForm {}
        }

        bpCard {
            styledH2 {
                css { +GameBrowserStyles.cardTitle }
                +"Join a Game"
            }
            gameList()
        }
    }
}

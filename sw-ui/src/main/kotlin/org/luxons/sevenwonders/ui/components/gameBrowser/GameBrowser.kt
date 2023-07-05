package org.luxons.sevenwonders.ui.components.gameBrowser

import blueprintjs.core.*
import emotion.react.*
import org.luxons.sevenwonders.ui.components.*
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import web.cssom.*

val GameBrowser = FC {
    div {
        css(GlobalStyles.fullscreen, GlobalStyles.zeusBackground) {
            padding = Padding(all = 1.rem)
        }
        div {
            css(ClassName(Classes.DARK)) {
                margin = Margin(vertical = 0.px, horizontal = Auto.auto)
                maxWidth = GlobalStyles.preGameWidth
            }
            div {
                css {
                    display = Display.flex
                    justifyContent = JustifyContent.spaceBetween
                }
                h1 { +"Games" }
                CurrentPlayerInfo()
            }

            BpCard {
                css {
                    marginBottom = 1.rem
                }

                h2 {
                    css {
                        marginTop = 0.px
                    }
                    +"Create a Game"
                }
                CreateGameForm()
            }

            BpCard {
                h2 {
                    css {
                        marginTop = 0.px
                    }
                    +"Join a Game"
                }
                GameList()
            }
        }
    }
}

val CurrentPlayerInfo = FC {
    val connectedPlayer = useSwSelector { it.connectedPlayer }
    PlayerInfo {
        player = connectedPlayer
        iconSize = 30
        showUsername = true
        orientation = FlexDirection.row
        ellipsize = false
    }
}

package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.cards.*
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import web.cssom.*
import web.cssom.Position

fun ChildrenBuilder.handRotationIndicator(direction: HandRotationDirection) {
    div {
        css {
            position = Position.absolute
            display = Display.flex
            alignItems = AlignItems.center
            bottom = 25.vh
            val sideDistance = 2.rem
            when (direction) {
                HandRotationDirection.LEFT -> left = sideDistance
                HandRotationDirection.RIGHT -> right = sideDistance
            }
        }

        title = "Your hand will be passed to the player on your $direction after playing this card."

        when (direction) {
            HandRotationDirection.LEFT -> {
                BpIcon {
                    icon = IconNames.ARROW_LEFT
                    size = 25
                }
                handCardsImg()
            }
            HandRotationDirection.RIGHT -> {
                handCardsImg()
                BpIcon {
                    icon = IconNames.ARROW_RIGHT
                    size = 25
                }
            }
        }
    }
}

private fun ChildrenBuilder.handCardsImg() {
    img {
        src = "images/hand-cards5.png"
        css {
            width = 4.rem
        }
    }
}

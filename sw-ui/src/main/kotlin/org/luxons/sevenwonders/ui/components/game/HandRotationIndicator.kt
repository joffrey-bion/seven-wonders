package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.bpIcon
import kotlinx.css.*
import kotlinx.html.title
import org.luxons.sevenwonders.model.cards.HandRotationDirection
import react.RBuilder
import react.dom.attrs
import styled.css
import styled.styledDiv
import styled.styledImg

fun RBuilder.handRotationIndicator(direction: HandRotationDirection) {
    styledDiv {
        css {
            position = Position.absolute
            display = Display.flex
            alignItems = Align.center
            bottom = 25.vh
        }
        attrs {
            title = "Your hand will be passed to the player on your $direction after playing this card."
        }
        val sideDistance = 2.rem
        when (direction) {
            HandRotationDirection.LEFT -> {
                css { left = sideDistance }
                bpIcon("arrow-left", size = 25)
                handCardsImg()
            }
            HandRotationDirection.RIGHT -> {
                css { right = sideDistance }
                handCardsImg()
                bpIcon("arrow-right", size = 25)
            }
        }
    }
}

private fun RBuilder.handCardsImg() {
    styledImg(src = "images/hand-cards5.png") {
        css {
            width = 4.rem
        }
    }
}

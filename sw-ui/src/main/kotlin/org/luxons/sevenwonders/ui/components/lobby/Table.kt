package org.luxons.sevenwonders.ui.components.lobby

import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.DIV
import react.RBuilder
import styled.StyledDOMBuilder
import styled.animation
import styled.css
import styled.styledDiv

private const val FIRE_REFLECTION_COLOR = "#b85e00"

fun RBuilder.lobbyWoodenTable(diameter: LinearDimension, borderSize: LinearDimension = 20.px) {
    circle(diameter) {
        css {
            backgroundColor = Color("#3d1e0e")
        }
        circle(diameter = diameter - borderSize) {
            css {
                position = Position.absolute
                top = borderSize / 2
                left = borderSize / 2
                background = "linear-gradient(45deg, #88541e, #995645, #52251a)"
            }
        }

        // flame reflection coming from bottom-right
        overlayCircle(diameter) {
            css {
                background = "linear-gradient(-45deg, $FIRE_REFLECTION_COLOR 10%, transparent 50%)"
                opacityAnimation(duration = 1.3.s)
            }
        }
        // flame reflection coming from bottom-left
        overlayCircle(diameter) {
            css {
                background = "linear-gradient(45deg, $FIRE_REFLECTION_COLOR 20%, transparent 40%)"
                opacityAnimation(duration = 0.8.s)
            }
        }
    }
}

private fun RBuilder.overlayCircle(diameter: LinearDimension, block: StyledDOMBuilder<DIV>.() -> Unit) {
    circle(diameter) {
        css {
            position = Position.absolute
            top = 0.px
            left = 0.px
        }
        block()
    }
}

private fun RBuilder.circle(diameter: LinearDimension, block: StyledDOMBuilder<DIV>.() -> Unit) {
    styledDiv {
        css {
            width = diameter
            height = diameter
            borderRadius = 50.pct
        }
        block()
    }
}

private fun CssBuilder.opacityAnimation(duration: Time) {
    animation(
        duration = duration,
        direction = AnimationDirection.alternate,
        iterationCount = IterationCount.infinite,
        timing = cubicBezier(0.4, 0.4, 0.4, 2.0)
    ) {
        from {
            opacity = 0.0
        }
        to {
            opacity = 0.35
        }
    }
}

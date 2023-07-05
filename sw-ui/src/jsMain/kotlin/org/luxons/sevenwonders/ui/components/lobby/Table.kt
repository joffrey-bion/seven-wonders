package org.luxons.sevenwonders.ui.components.lobby

import csstype.*
import emotion.css.*
import emotion.react.*
import emotion.styled.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.ReactHTML.div
import web.cssom.*

private val FIRE_REFLECTION_COLOR = Color("#b85e00")

private external interface CircleProps : PropsWithChildren, PropsWithClassName {
    var diameter: Length
}

private val Circle = FC<CircleProps>("Circle") { props ->
    div {
        css(props.className) {
            width = props.diameter
            height = props.diameter
            borderRadius = 50.pct
        }
        child(props.children)
    }
}

private val OverlayCircle = Circle.styled {
    position = Position.absolute
    top = 0.px
    left = 0.px
}

external interface LobbyWoodenTableProps : Props {
    var diameter: Length
    var borderSize: Length
}

val LobbyWoodenTable = FC<LobbyWoodenTableProps>("LobbyWoodenTable") { props ->
    Circle {
        diameter = props.diameter

        css {
            backgroundColor = Color("#3d1e0e")
        }

        Circle {
            diameter = props.diameter - props.borderSize
            css {
                position = Position.absolute
                top = props.borderSize / 2
                left = props.borderSize / 2
                background = linearGradient(45.deg, Color("#88541e"), Color("#995645"), Color("#52251a"))
            }
        }

        // flame reflection coming from bottom-right
        OverlayCircle {
            diameter = props.diameter

            css {
                background =
                    linearGradient((-45).deg, stop(FIRE_REFLECTION_COLOR, 10.pct), stop(NamedColor.transparent, 50.pct))
                opacityAnimation(duration = 1.3.s)
            }
        }
        // flame reflection coming from bottom-left
        OverlayCircle {
            diameter = props.diameter

            css {
                background =
                    linearGradient(45.deg, stop(FIRE_REFLECTION_COLOR, 20.pct), stop(NamedColor.transparent, 40.pct))
                opacityAnimation(duration = 0.8.s)
            }
        }
    }
}

private fun PropertiesBuilder.opacityAnimation(duration: Time) {
    val keyframes = keyframes {
        from {
            opacity = number(0.0)
        }
        to {
            opacity = number(0.35)
        }
    }
    animation = Animation(
        name = keyframes,
        duration = duration,
        timingFunction = cubicBezier(0.4, 0.4, 0.4, 2.0),
    )
    animationDirection = AnimationDirection.alternate
    animationIterationCount = AnimationIterationCount.infinite
}

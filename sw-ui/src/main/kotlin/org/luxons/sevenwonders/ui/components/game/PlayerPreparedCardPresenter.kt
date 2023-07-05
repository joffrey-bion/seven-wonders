package org.luxons.sevenwonders.ui.components.game

import csstype.*
import emotion.css.*
import emotion.react.*
import org.luxons.sevenwonders.model.cards.*
import org.luxons.sevenwonders.ui.redux.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import web.cssom.*

external interface PlayerPreparedCardProps : Props {
    var playerDisplayName: String
    var username: String
}

val PlayerPreparedCard = FC<PlayerPreparedCardProps>("PlayerPreparedCard") { props ->
    val cardBack = useSwSelector { it.gameState?.preparedCardsByUsername?.get(props.username) }

    PlayerPreparedCardPresenter {
        this.playerDisplayName = props.playerDisplayName
        this.cardBack = cardBack
    }
}

external interface PlayerPreparedCardPresenterProps : Props {
    var playerDisplayName: String
    var cardBack: CardBack?
}

private val PlayerPreparedCardPresenter = FC<PlayerPreparedCardPresenterProps>("PlayerPreparedCardPresenter") { props ->
    val cardBack = props.cardBack
    val sideSize = 30.px
    div {
        css {
            width = sideSize
            height = sideSize
        }
        title = if (cardBack == null) {
            "${props.playerDisplayName} is still thinking…"
        } else {
            "${props.playerDisplayName} is ready to play this turn"
        }

        if (cardBack != null) {
            CardBackImage {
                this.cardBack = cardBack
                css {
                    maxHeight = sideSize
                }
            }
        } else {
            RotatingGear {
                css {
                    maxHeight = sideSize
                }
            }
        }
    }
}

private val RotatingGear = FC<PropsWithClassName> { props ->
    img {
        src = "images/gear-50.png"
        css(props.className) {
            animation = Animation(
                name = keyframes {
                    to {
                        transform = rotate(360.deg)
                    }
                },
                duration = 1.5.s,
                timingFunction = cubicBezier(0.2, 0.9, 0.7, 1.3),
            )
            animationIterationCount = AnimationIterationCount.infinite
        }
    }
}

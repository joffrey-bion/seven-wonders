package org.luxons.sevenwonders.ui.components.game

import csstype.*
import emotion.css.*
import org.luxons.sevenwonders.ui.utils.*

object GameStyles {

    val totalScore = ClassName {
        fontWeight = FontWeight.bold
    }

    val civilScore = scoreTagColorCss(Color("#2a73c9"))
    val scienceScore = scoreTagColorCss(Color("#0f9960"))
    val militaryScore = scoreTagColorCss(Color("#d03232"))
    val tradeScore = scoreTagColorCss(Color("#e2c11b"))
    val guildScore = scoreTagColorCss(Color("#663399"))
    val wonderScore = scoreTagColorCss(NamedColor.darkcyan)
    val goldScore = scoreTagColorCss(NamedColor.goldenrod)

    val sandBgColor = NamedColor.palegoldenrod


    val fullBoardPreview = ClassName {
        width = 40.vw
        height = 50.vh
    }

    val dimmedCard = ClassName {
        filter = brightness(60.pct) + grayscale(50.pct)
    }

    val transactionsSelector = ClassName {
        backgroundColor = sandBgColor
        width = 40.rem // default is 500px, we want to fit players on the side

        children(".bp4-dialog-header") {
            background = None.none // overrides default white background
        }
    }

    val bestPrice = ClassName {
        fontWeight = FontWeight.bold
        color = rgb(50, 120, 50)
        transform = rotate((-20).deg)
    }

    val discardMoveText = ClassName {
        display = Display.flex
        alignItems = AlignItems.center
        height = 3.rem
        fontSize = 2.rem
        color = NamedColor.goldenrod
        fontWeight = FontWeight.bold
        borderTop = Border(0.2.rem, LineStyle.solid, NamedColor.goldenrod)
        borderBottom = Border(0.2.rem, LineStyle.solid, NamedColor.goldenrod)
    }

    val scoreBoard = ClassName {
        backgroundColor = sandBgColor
    }

    private fun scoreTagColorCss(color: Color) = ClassName {
        backgroundColor = color
    }

    val pulsatingRed = ClassName {
        animation = Animation(
            name = keyframes {
                to {
                    boxShadow = BoxShadow(
                        inset = BoxShadowInset.inset,
                        offsetX = 0.px,
                        offsetY = 0.px,
                        blurRadius = 20.px,
                        spreadRadius = 8.px,
                        color = NamedColor.red,
                    )
                }
            },
            duration = 2.s,
        )
        animationIterationCount = AnimationIterationCount.infinite
        animationDirection = AnimationDirection.alternate
    }
}

package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet
import styled.animation

object GameStyles : StyleSheet("GameStyles", isStatic = true) {

    val totalScore by css {
        fontWeight = FontWeight.bold
    }

    val civilScore by scoreTagColorCss(Color("#2a73c9"))
    val scienceScore by scoreTagColorCss(Color("#0f9960"))
    val militaryScore by scoreTagColorCss(Color("#d03232"))
    val tradeScore by scoreTagColorCss(Color("#e2c11b"))
    val guildScore by scoreTagColorCss(Color("#663399"))
    val wonderScore by scoreTagColorCss(Color.darkCyan)
    val goldScore by scoreTagColorCss(Color.goldenrod)

    private val sandBgColor = Color.paleGoldenrod

    val fullBoardPreviewPopover by css {
        val bgColor = sandBgColor.withAlpha(0.7)
        backgroundColor = bgColor
        borderRadius = 0.5.rem
        padding(all = 0.5.rem)

        children(".bp3-popover-content") {
            background = "none" // overrides default white background
        }
        descendants(".bp3-popover-arrow-fill") {
            put("fill", bgColor.toString()) // overrides default white arrow
        }
        descendants(".bp3-popover-arrow::before") {
            // The popover arrow is implemented with a simple square rotated 45 degrees (like a rhombus).
            // Since we use a semi-transparent background, we can see the box shadow of the rest of the arrow through
            // the popover, and thus we see the square. This boxShadow(transparent) is to avoid that.
            boxShadow(Color.transparent)
        }
    }

    val fullBoardPreview by css {
        width = 40.vw
        height = 50.vh
    }

    val dimmedCard by css {
        filter = "brightness(60%) grayscale(50%)"
    }

    val transactionsSelector by css {
        backgroundColor = sandBgColor
        width = 40.rem // default is 500px, we want to fit players on the side

        children(".bp3-dialog-header") {
            background = "none" // overrides default white background
        }
    }

    val bestPrice by css {
        fontWeight = FontWeight.bold
        color = rgb(50, 120, 50)
        transform {
            rotate((-20).deg)
        }
    }

    val discardMoveText by css {
        display = Display.flex
        alignItems = Align.center
        height = 3.rem
        fontSize = 2.rem
        color = Color.goldenrod
        fontWeight = FontWeight.bold
        borderTop(0.2.rem, BorderStyle.solid, Color.goldenrod)
        borderBottom(0.2.rem, BorderStyle.solid, Color.goldenrod)
    }

    val scoreBoard by css {
        backgroundColor = sandBgColor
    }

    private fun scoreTagColorCss(color: Color) = css {
        backgroundColor = color
    }

    val pulsatingRed by css {
        animation(
            duration = 2.s,
            iterationCount = IterationCount.infinite,
            direction = AnimationDirection.alternate,
        ) {
            to {
                boxShadowInset(color = Color.red, blurRadius = 20.px, spreadRadius = 8.px)
            }
        }
    }
}

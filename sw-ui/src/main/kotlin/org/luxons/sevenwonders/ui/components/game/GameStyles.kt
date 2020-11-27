package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet

object GameStyles : StyleSheet("GameStyles", isStatic = true) {

    private val sandColor = Color.paleGoldenrod.withAlpha(0.7)

    val fullBoardPreviewPopover by css {
        val bgColor = sandColor
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
        backgroundColor = sandColor

        children(".bp3-dialog-header") {
            background = "none" // overrides default white background
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
        backgroundColor = Color.paleGoldenrod
    }
}

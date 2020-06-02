package org.luxons.sevenwonders.ui.components.game

import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet

object GameStyles : StyleSheet("GameStyles", isStatic = true) {

    val fullBoardPreviewPopover by css {
        val bgColor = Color.paleGoldenrod.withAlpha(0.7)
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

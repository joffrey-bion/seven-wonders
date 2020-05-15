package org.luxons.sevenwonders.ui.components

import kotlinx.css.Overflow
import kotlinx.css.Position
import kotlinx.css.bottom
import kotlinx.css.left
import kotlinx.css.overflow
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.transform
import kotlinx.css.properties.translate
import kotlinx.css.px
import kotlinx.css.right
import kotlinx.css.top
import styled.StyleSheet

object GlobalStyles : StyleSheet("GlobalStyles", isStatic = true) {

    val fullscreen by css {
        position = Position.fixed
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
        overflow = Overflow.hidden
    }

    val fixedCenter by css {
        position = Position.fixed
        left = 50.pct
        top = 50.pct
        transform {
            translate((-50).pct, (-50).pct)
        }
    }

    val centerInParent by css {
        position = Position.absolute
        left = 50.pct
        top = 50.pct
        transform {
            translate((-50).pct, (-50).pct)
        }
    }
}

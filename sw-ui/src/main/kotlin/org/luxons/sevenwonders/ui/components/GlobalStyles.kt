package org.luxons.sevenwonders.ui.components

import kotlinx.css.*
import kotlinx.css.properties.*
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

    val papyrusBackground by css {
        background = "url('images/backgrounds/papyrus.jpg')"
        backgroundSize = "cover"
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

    val noPadding by css {
        padding(all = 0.px)
    }
}

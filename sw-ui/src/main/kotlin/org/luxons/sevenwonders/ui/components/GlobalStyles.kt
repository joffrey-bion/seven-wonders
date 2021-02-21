package org.luxons.sevenwonders.ui.components

import kotlinx.css.*
import kotlinx.css.properties.*
import styled.StyleSheet

object GlobalStyles : StyleSheet("GlobalStyles", isStatic = true) {

    val preGameWidth = 60.rem

    val zeusBackground by css {
        background = "url('images/backgrounds/zeus-temple.jpg') center no-repeat"
        backgroundSize = "cover"
    }

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
        +centerLeftTopTransform
    }

    val centerInPositionedParent by css {
        position = Position.absolute
        +centerLeftTopTransform
    }

    val centerLeftTopTransform by css {
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

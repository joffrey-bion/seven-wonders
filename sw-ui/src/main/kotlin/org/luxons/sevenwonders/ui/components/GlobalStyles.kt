package org.luxons.sevenwonders.ui.components

import emotion.css.*
import org.luxons.sevenwonders.ui.utils.*
import web.cssom.*


object GlobalStyles {

    val preGameWidth = 60.rem

    val zeusBackground = ClassName {
        background = "url('images/backgrounds/zeus-temple.jpg') center no-repeat".unsafeCast<Background>()
        backgroundSize = BackgroundSize.cover
    }

    val fullscreen = ClassName {
        position = Position.fixed
        top = 0.px
        left = 0.px
        bottom = 0.px
        right = 0.px
        overflow = Overflow.hidden
    }

    val papyrusBackground = ClassName {
        background = "url('images/backgrounds/papyrus.jpg')".unsafeCast<Background>()
        backgroundSize = BackgroundSize.cover
    }

    val centerLeftTopTransform = ClassName {
        left = 50.pct
        top = 50.pct
        transform = translate((-50).pct, (-50).pct)
    }

    val fixedCenter = ClassName(centerLeftTopTransform) {
        position = Position.fixed
    }

    val centerInPositionedParent = ClassName(centerLeftTopTransform) {
        position = Position.absolute
    }

    val noPadding = ClassName {
        padding = Padding(all = 0.px)
    }
}

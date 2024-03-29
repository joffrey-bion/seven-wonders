package org.luxons.sevenwonders.ui.components.lobby

import emotion.css.*
import org.luxons.sevenwonders.ui.components.*
import web.cssom.*

object LobbyStyles {

    val contentContainer = ClassName {
        margin = Margin(vertical = 0.px, horizontal = Auto.auto)
        maxWidth = GlobalStyles.preGameWidth
    }

    val setupPanel = ClassName {
        position = Position.fixed
        top = 2.rem
        right = 1.rem
        width = 20.rem
    }
}

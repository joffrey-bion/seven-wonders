package org.luxons.sevenwonders.ui.components.lobby

import react.RBuilder
import react.dom.*

fun RBuilder.lobby(lobbyId: Long) = div {
    h1 {
        +"Lobby $lobbyId"
    }
    p {
        +"Can't wait to play!"
    }
}

package org.luxons.sevenwonders.ui.components.gameBrowser

import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.ui.redux.connectState
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.*

interface PlayerInfoProps : RProps {
    var connectedPlayer: ConnectedPlayer?
}

class PlayerInfoPresenter(props: PlayerInfoProps) : RComponent<PlayerInfoProps, RState>(props) {

    override fun RBuilder.render() {
        span {
            b {
                +"Username:"
            }
            props.connectedPlayer?.let {
                + " ${it.displayName}"
            }
        }
    }
}

fun RBuilder.playerInfo() = playerInfo {}

private val playerInfo = connectState(
    clazz = PlayerInfoPresenter::class,
    mapStateToProps = { state, _ ->
        connectedPlayer = state.connectedPlayer
    }
)

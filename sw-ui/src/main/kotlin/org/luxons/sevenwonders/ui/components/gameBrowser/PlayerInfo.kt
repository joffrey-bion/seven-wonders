package org.luxons.sevenwonders.ui.components.gameBrowser

import com.palantir.blueprintjs.bpIcon
import kotlinx.css.*
import org.luxons.sevenwonders.model.api.BasicPlayerInfo
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.ui.redux.connectState
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css
import styled.styledDiv
import styled.styledSpan

interface PlayerInfoProps : RProps {
    var player: BasicPlayerInfo?
    var showUsername: Boolean
    var iconSize: Int
}

class PlayerInfoPresenter(props: PlayerInfoProps) : RComponent<PlayerInfoProps, RState>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
                alignItems = Align.center
            }
            props.player?.let {
                bpIcon(name = it.icon?.name ?: "user", size = props.iconSize)
                if (props.showUsername) {
                    playerNameWithUsername(it.displayName, it.username)
                } else {
                    playerName(it.displayName)
                }
            }
        }
    }

    private fun RBuilder.playerName(displayName: String) {
        styledSpan {
            css {
                fontSize = 1.rem
                marginLeft = 0.4.rem
            }
            +displayName
        }
    }

    private fun RBuilder.playerNameWithUsername(displayName: String, username: String) {
        styledDiv {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                marginLeft = 0.4.rem
            }
            styledSpan {
                css {
                    fontSize = 1.rem
                }
                +displayName
            }
            styledSpan {
                css {
                    marginTop = 0.1.rem
                    color = Color.lightGray
                    fontSize = 0.8.rem
                }
                +"($username)"
            }
        }
    }
}

fun RBuilder.playerInfo(
    playerDTO: PlayerDTO,
    showUsername: Boolean = false,
    iconSize: Int = 30,
) = child(PlayerInfoPresenter::class) {
    attrs {
        this.player = playerDTO
        this.showUsername = showUsername
        this.iconSize = iconSize
    }
}

fun RBuilder.currentPlayerInfo() = playerInfo {}

private val playerInfo = connectState(
    clazz = PlayerInfoPresenter::class,
    mapStateToProps = { state, _ ->
        player = state.connectedPlayer
        showUsername = true
    },
)

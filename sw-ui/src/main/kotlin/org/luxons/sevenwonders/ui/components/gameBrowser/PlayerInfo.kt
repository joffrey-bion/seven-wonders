package org.luxons.sevenwonders.ui.components.gameBrowser

import com.palantir.blueprintjs.bpIcon
import kotlinx.css.*
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.ui.redux.connectState
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import styled.css
import styled.styledDiv
import styled.styledSpan

interface PlayerInfoProps : RProps {
    var connectedPlayer: ConnectedPlayer?
}

class PlayerInfoPresenter(props: PlayerInfoProps) : RComponent<PlayerInfoProps, RState>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
                alignItems = Align.center
            }
            props.connectedPlayer?.let {
                bpIcon(name = it.icon?.name ?: "user", size = 30)
                styledDiv {
                    css {
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        marginLeft = 0.3.rem
                    }
                    styledSpan {
                        css {
                            fontSize = 1.rem
                        }
                        +it.displayName
                    }
                    styledSpan {
                        css {
                            marginTop = 0.1.rem
                            color = Color.lightGray
                            fontSize = 0.8.rem
                        }
                        +"(${it.username})"
                    }
                }
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

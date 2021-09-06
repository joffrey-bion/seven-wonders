package org.luxons.sevenwonders.ui.components.gameBrowser

import blueprintjs.core.bpIcon
import kotlinx.css.*
import kotlinx.html.title
import org.luxons.sevenwonders.model.api.BasicPlayerInfo
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.ui.redux.connectState
import react.*
import react.dom.attrs
import styled.css
import styled.styledDiv
import styled.styledSpan

interface PlayerInfoProps : PropsWithChildren {
    var player: BasicPlayerInfo?
    var showUsername: Boolean
    var iconSize: Int
    var orientation: FlexDirection
    var ellipsize: Boolean
}

class PlayerInfoPresenter(props: PlayerInfoProps) : RComponent<PlayerInfoProps, State>(props) {

    override fun RBuilder.render() {
        styledDiv {
            css {
                display = Display.flex
                alignItems = Align.center
                flexDirection = props.orientation
            }
            props.player?.let {
                bpIcon(name = it.icon?.name ?: "user", size = props.iconSize)
                if (props.showUsername) {
                    playerNameWithUsername(it.displayName, it.username) {
                        iconSeparationMargin()
                    }
                } else {
                    playerName(it.displayName) {
                        iconSeparationMargin()
                    }
                }
            }
        }
    }

    private fun RBuilder.playerName(displayName: String, style: CssBuilder.() -> Unit = {}) {
        styledSpan {
            css {
                fontSize = 1.rem
                if (props.orientation == FlexDirection.column) {
                    textAlign = TextAlign.center
                }
                style()
            }
            // TODO replace by BlueprintJS's Text elements (built-in ellipsize based on width)
            val maxDisplayNameLength = 15
            if (props.ellipsize && displayName.length > maxDisplayNameLength) {
                attrs {
                    title = displayName
                }
                +displayName.ellipsize(maxDisplayNameLength)
            } else {
                +displayName
            }
        }
    }

    private fun String.ellipsize(maxLength: Int) = take(maxLength - 1) + "…"

    private fun CssBuilder.iconSeparationMargin() {
        val margin = 0.4.rem
        when (props.orientation) {
            FlexDirection.row -> marginLeft = margin
            FlexDirection.column -> marginTop = margin
            FlexDirection.rowReverse -> marginRight = margin
            FlexDirection.columnReverse -> marginBottom = margin
            else -> error("Unsupported orientation '${props.orientation}' for player info component")
        }
    }

    private fun RBuilder.playerNameWithUsername(
        displayName: String,
        username: String,
        style: CssBuilder.() -> Unit = {}
    ) {
        styledDiv {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                style()
            }
            playerName(displayName)
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
    player: PlayerDTO,
    showUsername: Boolean = false,
    iconSize: Int = 30,
    orientation: FlexDirection = FlexDirection.row,
    ellipsize: Boolean = true,
) = child(PlayerInfoPresenter::class) {
    attrs {
        this.player = player
        this.showUsername = showUsername
        this.iconSize = iconSize
        this.orientation = orientation
        this.ellipsize = ellipsize
    }
}

fun RBuilder.currentPlayerInfo() = playerInfo {}

private val playerInfo = connectState(
    clazz = PlayerInfoPresenter::class,
    mapStateToProps = { state, _ ->
        player = state.connectedPlayer
        iconSize = 30
        showUsername = true
        orientation = FlexDirection.row
        ellipsize = false
    },
)

package org.luxons.sevenwonders.ui.components.gameBrowser

import blueprintjs.core.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.api.*
import react.*
import react.State
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import web.cssom.*

external interface PlayerInfoProps : PropsWithChildren {
    var player: BasicPlayerInfo?
    var showUsername: Boolean?
    var iconSize: Int?
    var orientation: FlexDirection?
    var ellipsize: Boolean?
}

val PlayerInfo = PlayerInfoPresenter::class.react

private class PlayerInfoPresenter(props: PlayerInfoProps) : Component<PlayerInfoProps, State>(props) {

    override fun render() = div.create {
        val orientation = props.orientation ?: FlexDirection.row
        css {
            display = Display.flex
            alignItems = AlignItems.center
            flexDirection = orientation
        }
        props.player?.let {
            BpIcon {
                icon = it.icon?.name ?: "user"
                size = props.iconSize ?: 30
            }
            if (props.showUsername == true) {
                playerNameWithUsername(it.displayName, it.username) {
                    iconSeparationMargin(orientation)
                }
            } else {
                playerName(it.displayName) {
                    iconSeparationMargin(orientation)
                }
            }
        }
    }

    private fun ChildrenBuilder.playerName(displayName: String, style: PropertiesBuilder.() -> Unit = {}) {
        span {
            css {
                fontSize = 1.rem
                if (props.orientation == FlexDirection.column) {
                    textAlign = TextAlign.center
                }
                style()
            }
            // TODO replace by BlueprintJS's Text elements (built-in ellipsize based on width)
            val maxDisplayNameLength = 15
            val ellipsize = props.ellipsize ?: true
            if (ellipsize && displayName.length > maxDisplayNameLength) {
                title = displayName
                +displayName.ellipsize(maxDisplayNameLength)
            } else {
                +displayName
            }
        }
    }

    private fun String.ellipsize(maxLength: Int) = take(maxLength - 1) + "…"

    private fun PropertiesBuilder.iconSeparationMargin(orientation: FlexDirection) {
        val margin = 0.4.rem
        when (orientation) {
            FlexDirection.row -> marginLeft = margin
            FlexDirection.column -> marginTop = margin
            FlexDirection.rowReverse -> marginRight = margin
            FlexDirection.columnReverse -> marginBottom = margin
            else -> error("Unsupported orientation '$orientation' for player info component")
        }
    }

    private fun ChildrenBuilder.playerNameWithUsername(
        displayName: String,
        username: String,
        style: PropertiesBuilder.() -> Unit = {}
    ) {
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.column
                style()
            }
            playerName(displayName)
            span {
                css {
                    marginTop = 0.1.rem
                    color = NamedColor.lightgray
                    fontSize = 0.8.rem
                }
                +"($username)"
            }
        }
    }
}

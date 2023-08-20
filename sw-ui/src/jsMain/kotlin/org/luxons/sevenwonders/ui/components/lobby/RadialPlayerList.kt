package org.luxons.sevenwonders.ui.components.lobby

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.model.api.*
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.model.wonders.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import web.cssom.*
import web.html.*

fun ChildrenBuilder.radialPlayerList(
    players: List<PlayerDTO>,
    currentPlayer: PlayerDTO,
    block: HTMLAttributes<HTMLDivElement>.() -> Unit = {},
) {
    val playerItems = players //
        .map { PlayerItem.Player(it) }
        .growWithPlaceholders(targetSize = 3)
        .withUserFirst(currentPlayer)

    radialList(
        items = playerItems,
        centerElement = LobbyWoodenTable.create {
            diameter = 200.px
            borderSize = 15.px
        },
        renderItem = { PlayerElement.create { playerItem = it } },
        getKey = { it.key },
        itemWidth = 120,
        itemHeight = 100,
        options = RadialConfig(
            radius = 175,
            firstItemAngleDegrees = 180, // self at the bottom
            direction = Direction.COUNTERCLOCKWISE, // new players sit to the right of last player
        ),
        block = block,
    )
}

private fun List<PlayerItem>.growWithPlaceholders(targetSize: Int): List<PlayerItem> = when {
    size < targetSize -> this + List(targetSize - size) { PlayerItem.Placeholder(size + it) }
    else -> this
}

private fun List<PlayerItem>.withUserFirst(me: PlayerDTO): List<PlayerItem> {
    val nonUsersBeginning = takeWhile { (it as? PlayerItem.Player)?.player?.username != me.username }
    val userToEnd = subList(nonUsersBeginning.size, size)
    return userToEnd + nonUsersBeginning
}

private sealed class PlayerItem {
    abstract val key: String
    abstract val playerText: String
    abstract val opacity: Opacity
    abstract val icon: ReactElement<*>

    data class Player(val player: PlayerDTO) : PlayerItem() {
        override val key = player.username
        override val playerText = player.displayName
        override val opacity = number(1.0)
        override val icon = createUserIcon(
            icon = player.icon ?: when {
                player.isGameOwner -> Icon(IconNames.BADGE)
                else -> Icon(IconNames.USER)
            },
            title = if (player.isGameOwner) "Game owner" else null,
        )
    }

    data class Placeholder(val index: Int) : PlayerItem() {
        override val key = "player-placeholder-$index"
        override val playerText = "?"
        override val opacity = number(0.4)
        override val icon = createUserIcon(
            icon = Icon(IconNames.USER),
            title = "Waiting for player...",
        )
    }
}

private fun createUserIcon(icon: Icon, title: String?) = BpIcon.create {
    this.icon = icon.name
    this.size = 50
    this.title = title
}

private external interface PlayerElementProps : Props {
    var playerItem: PlayerItem
}

private val PlayerElement = FC<PlayerElementProps>(displayName = "PlayerElement") { props ->
    val playerItem = props.playerItem
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
            opacity = playerItem.opacity
        }
        +playerItem.icon
        span {
            css {
                fontSize = if (playerItem is PlayerItem.Placeholder) 1.5.rem else 0.9.rem
            }
            +playerItem.playerText
        }
        if (playerItem is PlayerItem.Player) {
            div {
                val wonder = playerItem.player.wonder

                css {
                    marginTop = 0.3.rem

                    children(".wonder-tag") {
                        color = Color("#f5f8fa") // blueprintjs dark theme color (removed by .bp4-tag)
                        backgroundColor = when (wonder.side) {
                            WonderSide.A -> NamedColor.seagreen
                            WonderSide.B -> NamedColor.darkred
                        }
                    }
                }

                BpTag {
                    round = true
                    className = ClassName("wonder-tag")

                    +"${wonder.name} ${wonder.side}"
                }
            }
        }
    }
}

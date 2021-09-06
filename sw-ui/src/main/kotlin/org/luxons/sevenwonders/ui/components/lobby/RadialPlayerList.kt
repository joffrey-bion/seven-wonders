package org.luxons.sevenwonders.ui.components.lobby

import blueprintjs.core.bpIcon
import blueprintjs.core.bpTag
import kotlinx.css.*
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.model.wonders.WonderSide
import react.RBuilder
import react.ReactElement
import react.buildElement
import styled.*

fun RBuilder.radialPlayerList(
    players: List<PlayerDTO>,
    currentPlayer: PlayerDTO,
    block: StyledDOMBuilder<DIV>.() -> Unit = {},
) {
    val playerItems = players //
        .map { PlayerItem.Player(it) }
        .growWithPlaceholders(targetSize = 3)
        .withUserFirst(currentPlayer)

    val tableImg = buildElement { lobbyWoodenTable(diameter = 200.px, borderSize = 15.px) }

    radialList(
        items = playerItems,
        centerElement = tableImg,
        renderItem = { buildElement { playerElement(it) } },
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
    abstract val opacity: Double
    abstract val icon: ReactElement

    data class Player(val player: PlayerDTO) : PlayerItem() {
        override val key = player.username
        override val playerText = player.displayName
        override val opacity = 1.0
        override val icon = buildElement {
            userIcon(
                icon = player.icon ?: when {
                    player.isGameOwner -> Icon("badge")
                    else -> Icon("user")
                },
                title = if (player.isGameOwner) "Game owner" else null,
            )
        }
    }

    data class Placeholder(val index: Int) : PlayerItem() {
        override val key = "player-placeholder-$index"
        override val playerText = "?"
        override val opacity = 0.4
        override val icon = buildElement {
            userIcon(
                icon = Icon("user"),
                title = "Waiting for player...",
            )
        }
    }
}

private fun RBuilder.userIcon(icon: Icon, title: String?) = bpIcon(
    name = icon.name,
    size = 50,
    title = title,
)

private fun RBuilder.playerElement(playerItem: PlayerItem) {
    styledDiv {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = Align.center
            opacity = playerItem.opacity
        }
        child(playerItem.icon)
        styledSpan {
            css {
                fontSize = if (playerItem is PlayerItem.Placeholder) 1.5.rem else 0.9.rem
            }
            +playerItem.playerText
        }
        if (playerItem is PlayerItem.Player) {
            styledDiv {
                val wonder = playerItem.player.wonder
                css {
                    marginTop = 0.3.rem

                    // this is to overcome ".bp3-dark .bp3-tag" on the nested bpTag
                    children(".wonder-tag") {
                        color = Color("#f5f8fa") // blueprintjs dark theme color (removed by .bp3-tag)
                        backgroundColor = when (wonder.side) {
                            WonderSide.A -> Color.seaGreen
                            WonderSide.B -> Color.darkRed
                        }
                    }
                }
                bpTag(round = true, className = "wonder-tag") {
                    +"${wonder.name} ${wonder.side}"
                }
            }
        }
    }
}

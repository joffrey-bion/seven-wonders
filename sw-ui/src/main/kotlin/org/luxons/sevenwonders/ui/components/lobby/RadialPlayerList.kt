package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.bpIcon
import com.palantir.blueprintjs.bpTag
import kotlinx.css.*
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.actions.Icon
import org.luxons.sevenwonders.model.wonders.WonderSide
import react.RBuilder
import react.ReactElement
import react.buildElement
import react.dom.*
import styled.*

fun RBuilder.radialPlayerList(players: List<PlayerDTO>, currentPlayer: PlayerDTO): ReactElement {
    val playerItems = players //
        .map { PlayerItem.Player(it, it.username == currentPlayer.username) }
        .growWithPlaceholders(targetSize = 3)
        .withUserFirst(currentPlayer)

    val tableImg = buildElement { roundTableImg() }

    return radialList(
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
    )
}

private fun RBuilder.roundTableImg(): ReactElement = img(src = "images/round-table.png", alt = "Round table") {
    attrs {
        width = "200"
        height = "200"
    }
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
    abstract val wonderText: String
    abstract val opacity: Double
    abstract val icon: ReactElement

    data class Player(val player: PlayerDTO, val isMe: Boolean) : PlayerItem() {
        override val key = player.username
        override val playerText = player.displayName
        override val wonderText = "${player.wonder.name} ${player.wonder.side.name}"
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
        override val wonderText = " "
        override val opacity = 0.4
        override val icon = buildElement {
            userIcon(
                icon = Icon("user"),
                title = "Waiting for player...",
            )
        }
    }
}

private fun RBuilder.userIcon(icon: Icon, title: String?): ReactElement = bpIcon(
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
                margin(all = 0.px)
                textAlign = TextAlign.center
                fontSize = if (playerItem is PlayerItem.Placeholder) 1.5.rem else 0.9.rem
                fontWeight = FontWeight.bold
            }
            +playerItem.playerText
        }
        if (playerItem is PlayerItem.Player) {
            styledDiv {
                css {
                    margin(top = 0.3.rem)
                }
                bpTag(round = true) {
                    attrs {
                        className = LobbyStyles.getClassName {
                            when (playerItem.player.wonder.side) {
                                WonderSide.A -> it::wonderTagSideA
                                WonderSide.B -> it::wonderTagSideB
                            }
                        }
                    }
                    +playerItem.wonderText
                }
            }
        }
    }
}

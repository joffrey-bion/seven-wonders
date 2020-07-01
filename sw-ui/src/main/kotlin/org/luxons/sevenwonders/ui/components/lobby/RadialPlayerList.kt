package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpIcon
import kotlinx.css.*
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.actions.Icon
import react.*
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledH4

private sealed class PlayerItem {
    abstract val username: String

    data class Player(val player: PlayerDTO) : PlayerItem() {
        override val username = player.username
    }
    data class Placeholder(val index: Int) : PlayerItem() {
        override val username = "player-placeholder-$index"
    }
}

fun RBuilder.radialPlayerList(players: List<PlayerDTO>, currentPlayer: PlayerDTO): ReactElement {
    val playerItems = players
        .growWithPlaceholders(targetSize = 3)
        .withUserFirst(currentPlayer)

    val tableImg = buildElement { roundTableImg() }

    return radialList(
        items = playerItems,
        centerElement = tableImg,
        renderItem = { it.renderAsListItem(it.username == currentPlayer.username) },
        getKey = { it.username },
        itemWidth = 120,
        itemHeight = 100,
        options = RadialConfig(
            radius = 175,
            firstItemAngleDegrees = 180, // self at the bottom
            direction = Direction.COUNTERCLOCKWISE // new players sit to the right of last player
        )
    )
}

private fun RBuilder.roundTableImg(): ReactElement = img(src = "images/round-table.png", alt = "Round table") {
    attrs {
        width = "200"
        height = "200"
    }
}

private fun List<PlayerItem>.withUserFirst(me: PlayerDTO): List<PlayerItem> {
    val nonUsersBeginning = takeWhile { (it as? PlayerItem.Player)?.player?.username != me.username }
    val userToEnd = subList(nonUsersBeginning.size, size)
    return userToEnd + nonUsersBeginning
}

private fun List<PlayerDTO>.growWithPlaceholders(targetSize: Int): List<PlayerItem> {
    val items = map { PlayerItem.Player(it) }
    return if (size >= targetSize) {
        items
    } else {
        items + List(targetSize - size) { PlayerItem.Placeholder(size + it) }
    }
}

private fun PlayerItem.renderAsListItem(isMe: Boolean): ReactElement = when (this) {
    is PlayerItem.Placeholder -> buildElement { playerPlaceholder() }
    is PlayerItem.Player -> buildElement { playerItem(this@renderAsListItem.player, isMe) }
}

private fun RBuilder.playerItem(player: PlayerDTO, isMe: Boolean): ReactElement = styledDiv {
    css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = Align.center
    }
    val title = if (player.isGameOwner) "Game owner" else null
    val icon = player.icon ?: when {
        player.isGameOwner -> Icon("badge")
        else -> Icon("user")
    }
    userIcon(isMe = isMe, icon = icon, title = title)
    styledH4 {
        css {
            margin(all = 0.px)
        }
        +player.displayName
    }
    styledDiv {
        css {
            margin(top = 0.3.rem)
        }
        +"${player.wonder.name} (${player.wonder.side.name})"
    }
}

private fun RBuilder.playerPlaceholder(): ReactElement = styledDiv {
    css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = Align.center
        opacity = 0.3
    }
    userIcon(isMe = false, icon = Icon("user"), title = "Waiting for player...")
    styledH4 {
        css {
            margin = "0"
        }
        +"?"
    }
    styledDiv {
        css {
            margin(top = 0.3.rem)
        }
        +" " // placeholder for wonder name
    }
}

private fun RBuilder.userIcon(isMe: Boolean, icon: Icon, title: String?): ReactElement {
    val intent: Intent = if (isMe) Intent.WARNING else Intent.NONE
    return bpIcon(name = icon.name, intent = intent, size = 50, title = title)
}

package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpIcon
import kotlinx.css.*
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.actions.Icon
import react.RBuilder
import react.ReactElement
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledH5

fun RBuilder.radialPlayerList(players: List<PlayerDTO>, currentPlayer: PlayerDTO): ReactElement {
    val playerItemBuilders = players
        .growTo(targetSize = 3)
        .withUserFirst(currentPlayer)
        .map { p -> p.elementBuilder(p?.username == currentPlayer.username) }

    val tableImgBuilder: ElementBuilder = { roundTableImg() }

    return radialList(
        itemBuilders = playerItemBuilders,
        centerElementBuilder = tableImgBuilder,
        itemWidth = 120,
        itemHeight = 100,
        options = RadialConfig(
            radius = 175,
            firstItemAngleDegrees = 180, // self at the bottom
            direction = Direction.COUNTERCLOCKWISE // new players sit to the right of last player
        )
    )
}

private fun RBuilder.roundTableImg(): ReactElement = img {
    attrs {
        src = "images/round-table.png"
        alt = "Round table"
        width = "200"
        height = "200"
    }
}

private fun List<PlayerDTO?>.withUserFirst(me: PlayerDTO): List<PlayerDTO?> {
    val nonUsersBeginning = takeWhile { it?.username != me.username }
    val userToEnd = subList(nonUsersBeginning.size, size)
    return userToEnd + nonUsersBeginning
}

private fun <T> List<T>.growTo(targetSize: Int): List<T?> {
    if (size >= targetSize) return this
    return this + List(targetSize - size) { null }
}

private fun PlayerDTO?.elementBuilder(isMe: Boolean): ElementBuilder {
    if (this == null) {
        return { playerPlaceholder() }
    } else {
        return { playerItem(this@elementBuilder, isMe) }
    }
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
    styledH5 {
        css {
            margin = "0"
        }
        +player.displayName
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
    styledH5 {
        css {
            margin = "0"
        }
        +"?"
    }
}

private fun RBuilder.userIcon(isMe: Boolean, icon: Icon, title: String?): ReactElement {
    val intent: Intent = if (isMe) Intent.WARNING else Intent.NONE
    return bpIcon(name = icon.name, intent = intent, size = 50, title = title)
}

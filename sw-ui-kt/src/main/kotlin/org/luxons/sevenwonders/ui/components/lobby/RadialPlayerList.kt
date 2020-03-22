package org.luxons.sevenwonders.ui.components.lobby

import com.palantir.blueprintjs.IconName
import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.icon
import kotlinx.css.Align
import kotlinx.css.Display
import kotlinx.css.FlexDirection
import kotlinx.css.alignItems
import kotlinx.css.display
import kotlinx.css.flexDirection
import kotlinx.css.margin
import kotlinx.css.opacity
import org.luxons.sevenwonders.model.api.PlayerDTO
import react.RBuilder
import react.ReactElement
import react.dom.*
import styled.css
import styled.styledDiv
import styled.styledH5

fun RBuilder.radialPlayerList(players: List<PlayerDTO>): ReactElement {
    val playerItemBuilders = players
        .withUserFirst()
        .growTo(targetSize = 3)
        .map { p -> p.elementBuilder() }

    val tableImgBuilder: ElementBuilder = { roundTableImg() }

    return radialList(
        itemBuilders = playerItemBuilders,
        centerElementBuilder = tableImgBuilder,
        itemWidth = 120,
        itemHeight = 100,
        options = RadialConfig(
            radius = 175,
            offsetDegrees = 180
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

private fun List<PlayerDTO>.withUserFirst(): List<PlayerDTO> {
    val nonUsersBeginning = takeWhile { !it.isUser }
    val userToEnd = subList(nonUsersBeginning.size, size)
    return userToEnd + nonUsersBeginning
}

private fun <T> List<T>.growTo(targetSize: Int): List<T?> {
    if (size >= targetSize) return this
    return this + List(targetSize - size) { null }
}

private fun PlayerDTO?.elementBuilder(): ElementBuilder {
    if (this == null) {
        return { playerPlaceholder() }
    } else {
        return { playerItem(this@elementBuilder) }
    }
}

private fun RBuilder.playerItem(player: PlayerDTO): ReactElement = styledDiv {
    css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = Align.center
    }
    val title = if (player.isGameOwner) "Game owner" else null
    userIcon(isUser = player.isUser, isOwner = player.isGameOwner, title = title)
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
    userIcon(isUser = false, isOwner = false, title = "Waiting for player...")
    styledH5 {
        css {
           margin = "0"
        }
        +"?"
    }
}

private fun RBuilder.userIcon(isUser: Boolean, isOwner: Boolean, title: String?): ReactElement {
    val iconName: IconName = if (isOwner) "badge" else "user"
    val intent: Intent = if (isUser) Intent.WARNING else Intent.NONE
    return icon(name = iconName, intent = intent, size = 50, title = title)
}

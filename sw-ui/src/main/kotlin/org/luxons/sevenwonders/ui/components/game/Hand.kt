package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.Intent
import com.palantir.blueprintjs.bpButton
import com.palantir.blueprintjs.bpButtonGroup
import kotlinx.css.Align
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.Display
import kotlinx.css.GridColumn
import kotlinx.css.GridRow
import kotlinx.css.Position
import kotlinx.css.alignItems
import kotlinx.css.bottom
import kotlinx.css.display
import kotlinx.css.filter
import kotlinx.css.gridColumn
import kotlinx.css.gridRow
import kotlinx.css.height
import kotlinx.css.left
import kotlinx.css.margin
import kotlinx.css.maxHeight
import kotlinx.css.maxWidth
import kotlinx.css.pct
import kotlinx.css.position
import kotlinx.css.properties.*
import kotlinx.css.px
import kotlinx.css.rem
import kotlinx.css.vh
import kotlinx.css.vw
import kotlinx.css.width
import kotlinx.css.zIndex
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.model.wonders.WonderBuildability
import react.RBuilder
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv
import kotlin.math.absoluteValue

fun RBuilder.handComponent(
    cards: List<HandCard>,
    wonderBuildability: WonderBuildability,
    preparedMove: PlayerMove?,
    prepareMove: (PlayerMove) -> Unit
) {
    styledDiv {
        css {
            handStyle()
        }
        cards.filter { it.name != preparedMove?.cardName }.forEachIndexed { index, c ->
            handCard(
                card = c,
                wonderBuildability = wonderBuildability,
                prepareMove = prepareMove
            ) {
                attrs {
                    key = index.toString()
                }
            }
        }
    }
}

private fun RBuilder.handCard(
    card: HandCard,
    wonderBuildability: WonderBuildability,
    prepareMove: (PlayerMove) -> Unit,
    block: StyledDOMBuilder<DIV>.() -> Unit
) {
    styledDiv {
        css {
            handCardStyle()
        }
        block()
        cardImage(card) {
            css {
                handCardImgStyle(card.playability.isPlayable)
            }
        }
        actionButtons(card, wonderBuildability, prepareMove)
    }
}

private fun RBuilder.actionButtons(card: HandCard, wonderBuildability: WonderBuildability, prepareMove: (PlayerMove) -> Unit) {
    // class: action-buttons
    styledDiv {
        css {
            alignItems = Align.flexEnd
            display = Display.none
            gridRow = GridRow("1")
            gridColumn = GridColumn("1")

            ancestorHover(".hand-card") {
                display = Display.flex
            }
        }
        bpButtonGroup {
            bpButton(title = "PLAY (${priceText(-card.playability.minPrice)})",
                large = true,
                intent = Intent.SUCCESS,
                icon = "play",
                disabled = !card.playability.isPlayable,
                onClick = {
                    val transactions = card.playability.cheapestTransactions.firstOrNull() ?: noTransactions()
                    prepareMove(PlayerMove(MoveType.PLAY, card.name, transactions))
                }
            )
            bpButton(title = "UPGRADE WONDER (${priceText(-wonderBuildability.minPrice)})",
                large = true,
                intent = Intent.PRIMARY,
                icon = "key-shift",
                disabled = !wonderBuildability.isBuildable,
                onClick = {
                    val wonderTransactions = wonderBuildability.cheapestTransactions.firstOrNull() ?: noTransactions()
                    prepareMove(PlayerMove(MoveType.UPGRADE_WONDER, card.name, wonderTransactions))
                }
            )
            bpButton(title = "DISCARD (+3 coins)", // TODO remove hardcoded value
                large = true,
                intent = Intent.DANGER,
                icon = "cross",
                onClick = { prepareMove(PlayerMove(MoveType.DISCARD, card.name)) }
            )
        }
    }
}

private fun priceText(amount: Int) = when (amount.absoluteValue) {
    0 -> "free"
    1 -> "$amount coin"
    else -> "$amount coins"
}

private fun CSSBuilder.handStyle() {
    alignItems = Align.center
    bottom = 0.px
    display = Display.flex
    height = 345.px
    left = 50.pct
    maxHeight = 25.vw
    position = Position.absolute
    transform {
        translate(tx = (-50).pct, ty = 55.pct)
    }
    transition(duration = 0.5.s)
    zIndex = 30

    hover {
        bottom = 4.rem
        transform {
            translate(tx = (-50).pct, ty = 0.pct)
        }
    }
}

private fun CSSBuilder.handCardStyle() {
    classes.add("hand-card")
    alignItems = Align.flexEnd
    display = Display.grid
    margin(all = 0.2.rem)
}

private fun CSSBuilder.handCardImgStyle(isPlayable: Boolean) {
    gridRow = GridRow("1")
    gridColumn = GridColumn("1")
    maxWidth = 13.vw
    maxHeight = 60.vh
    transition(duration = 0.1.s)
    width = 11.rem

    ancestorHover(".hand-card") {
        boxShadow(offsetX = 0.px, offsetY = 10.px, blurRadius = 40.px, color = Color.black)
        width = 14.rem
        maxWidth = 15.vw
        maxHeight = 90.vh
    }

    if (!isPlayable) {
        filter = "grayscale(50%) contrast(50%)"
    }
}

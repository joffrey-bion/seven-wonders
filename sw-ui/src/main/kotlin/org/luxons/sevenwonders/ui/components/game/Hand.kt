package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.*
import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.cards.CardPlayability
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.model.wonders.WonderBuildability
import react.RBuilder
import react.RElementBuilder
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
            playCardButton(card, prepareMove)
            upgradeWonderButton(wonderBuildability, prepareMove, card)
            discardButton(prepareMove, card)
        }
    }
}

private fun RElementBuilder<IButtonGroupProps>.playCardButton(
    card: HandCard,
    prepareMove: (PlayerMove) -> Unit
) {
    bpButton(
        title = "PLAY (${cardPlayabilityInfo(card.playability)})",
        large = true,
        intent = Intent.SUCCESS,
        disabled = !card.playability.isPlayable,
        onClick = {
            val transactions = card.playability.cheapestTransactions.firstOrNull() ?: noTransactions()
            prepareMove(PlayerMove(MoveType.PLAY, card.name, transactions))
        }
    ) {
        bpIcon("play")
        if (card.playability.isPlayable && !card.playability.isFree) {
            priceInfo(card.playability.minPrice)
        }
    }
}

private fun RElementBuilder<IButtonGroupProps>.upgradeWonderButton(
    wonderBuildability: WonderBuildability,
    prepareMove: (PlayerMove) -> Unit,
    card: HandCard
) {
    bpButton(
        title = "UPGRADE WONDER (${wonderBuildabilityInfo(wonderBuildability)})",
        large = true,
        intent = Intent.PRIMARY,
        disabled = !wonderBuildability.isBuildable,
        onClick = {
            val transactions = wonderBuildability.cheapestTransactions.firstOrNull() ?: noTransactions()
            prepareMove(PlayerMove(MoveType.UPGRADE_WONDER, card.name, transactions))
        }
    ) {
        bpIcon("key-shift")
        if (wonderBuildability.isBuildable && !wonderBuildability.isFree) {
            priceInfo(wonderBuildability.minPrice)
        }
    }
}

private fun RElementBuilder<IButtonGroupProps>.discardButton(
    prepareMove: (PlayerMove) -> Unit,
    card: HandCard
) {
    bpButton(
        title = "DISCARD (+3 coins)", // TODO remove hardcoded value
        large = true,
        intent = Intent.DANGER,
        icon = "cross",
        onClick = { prepareMove(PlayerMove(MoveType.DISCARD, card.name)) }
    )
}

private fun cardPlayabilityInfo(playability: CardPlayability) = when(playability.isPlayable) {
    true -> priceText(-playability.minPrice)
    false -> playability.playabilityLevel.message
}

private fun wonderBuildabilityInfo(buildability: WonderBuildability) = when(buildability.isBuildable) {
    true -> priceText(-buildability.minPrice)
    false -> buildability.playabilityLevel.message
}

private fun priceText(amount: Int) = when (amount.absoluteValue) {
    0 -> "free"
    1 -> "${pricePrefix(amount)}$amount coin"
    else -> "${pricePrefix(amount)}$amount coins"
}

private fun pricePrefix(amount: Int) = when {
    amount > 0 -> "+"
    else -> ""
}

private fun RElementBuilder<IButtonProps>.priceInfo(amount: Int) {
    styledDiv {
        val size = 1.rem
        css {
            position = Position.absolute
            top = (-0.2).rem
            left = (-0.2).rem
            backgroundColor = Color.goldenrod
            width = size
            height = size
            borderRadius = size
            fontSize = size * 0.8
            textAlign = TextAlign.center
            zIndex = 5 // above all 3 buttons
        }
        +"$amount"
    }
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

package org.luxons.sevenwonders.ui.components.game

import com.palantir.blueprintjs.*
import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.cards.CardPlayability
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.resources.PricedResourceTransactions
import org.luxons.sevenwonders.model.wonders.WonderBuildability
import org.luxons.sevenwonders.ui.redux.TransactionSelectorState
import react.*
import styled.StyledDOMBuilder
import styled.css
import styled.styledDiv
import kotlin.math.absoluteValue

private enum class HandAction(
    val buttonTitle: String,
    val moveType: MoveType,
    val icon: IconName,
) {
    PLAY("PLAY", MoveType.PLAY, "play"),
    PLAY_FREE("Play as this age's free card", MoveType.PLAY_FREE, "star"),
    PLAY_FREE_DISCARDED("Play discarded card", MoveType.PLAY_FREE_DISCARDED, "star"),
    COPY_GUILD("Copy this guild card", MoveType.COPY_GUILD, "duplicate")
}

interface HandProps : RProps {
    var turnInfo: PlayerTurnInfo
    var preparedMove: PlayerMove?
    var prepareMove: (PlayerMove) -> Unit
    var startTransactionsSelection: (TransactionSelectorState) -> Unit
}

class HandComponent(props: HandProps) : RComponent<HandProps, RState>(props) {

    override fun RBuilder.render() {
        val hand = props.turnInfo.cardsToPlay() ?: return
        styledDiv {
            css {
                handStyle()
            }
            hand.filter { it.name != props.preparedMove?.cardName }.forEachIndexed { index, c ->
                handCard(card = c) {
                    attrs {
                        key = index.toString()
                    }
                }
            }
        }
    }

    private fun PlayerTurnInfo.cardsToPlay(): List<HandCard>? = when (action) {
        Action.PLAY, Action.PLAY_2, Action.PLAY_LAST -> hand
        Action.PLAY_FREE_DISCARDED -> discardedCards
        Action.PICK_NEIGHBOR_GUILD -> neighbourGuildCards
        Action.WAIT, Action.WATCH_SCORE, Action.SAY_READY -> null
    }

    private fun RBuilder.handCard(
        card: HandCard,
        block: StyledDOMBuilder<DIV>.() -> Unit,
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
            actionButtons(card)
        }
    }

    private fun RBuilder.actionButtons(card: HandCard) {
        styledDiv {
            css {
                justifyContent = JustifyContent.center
                alignItems = Align.flexEnd
                display = Display.none
                gridRow = GridRow("1")
                gridColumn = GridColumn("1")

                ancestorHover(".hand-card") {
                    display = Display.flex
                }
            }
            bpButtonGroup {
                val action = props.turnInfo.action
                when (action) {
                    Action.PLAY, Action.PLAY_2, Action.PLAY_LAST -> {
                        playCardButton(card, HandAction.PLAY)
                        if (props.turnInfo.getOwnBoard().canPlayAnyCardForFree) {
                            playCardButton(card.copy(playability = CardPlayability.SPECIAL_FREE), HandAction.PLAY_FREE)
                        }
                    }
                    Action.PLAY_FREE_DISCARDED -> playCardButton(card, HandAction.PLAY_FREE_DISCARDED)
                    Action.PICK_NEIGHBOR_GUILD -> playCardButton(card, HandAction.COPY_GUILD)
                    else -> error("unsupported action in hand card: $action")
                }

                if (action.allowsBuildingWonder()) {
                    upgradeWonderButton(card)
                }
                if (action.allowsDiscarding()) {
                    discardButton(card)
                }
            }
        }
    }

    private fun RElementBuilder<IButtonGroupProps>.playCardButton(card: HandCard, handAction: HandAction) {
        bpButton(
            title = "${handAction.buttonTitle} (${cardPlayabilityInfo(card.playability)})",
            large = true,
            intent = Intent.SUCCESS,
            disabled = !card.playability.isPlayable,
            onClick = { prepareMove(handAction.moveType, card, card.playability.cheapestTransactions) },
        ) {
            bpIcon(handAction.icon)
            if (card.playability.isPlayable && !card.playability.isFree) {
                priceInfo(card.playability.minPrice)
            }
        }
    }

    private fun RElementBuilder<IButtonGroupProps>.upgradeWonderButton(card: HandCard) {
        val wonderBuildability = props.turnInfo.wonderBuildability
        bpButton(
            title = "UPGRADE WONDER (${wonderBuildabilityInfo(wonderBuildability)})",
            large = true,
            intent = Intent.PRIMARY,
            disabled = !wonderBuildability.isBuildable,
            onClick = { prepareMove(MoveType.UPGRADE_WONDER, card, wonderBuildability.cheapestTransactions) },
        ) {
            bpIcon("key-shift")
            if (wonderBuildability.isBuildable && !wonderBuildability.isFree) {
                priceInfo(wonderBuildability.minPrice)
            }
        }
    }

    private fun prepareMove(moveType: MoveType, card: HandCard, transactions: Set<PricedResourceTransactions>) {
        when (transactions.size) {
            1 -> props.prepareMove(PlayerMove(moveType, card.name, transactions.first()))
            else -> props.startTransactionsSelection(TransactionSelectorState(moveType, card, transactions))
        }
    }

    private fun RElementBuilder<IButtonGroupProps>.discardButton(card: HandCard) {
        bpButton(
            title = "DISCARD (+3 coins)", // TODO remove hardcoded value
            large = true,
            intent = Intent.DANGER,
            icon = "cross",
            onClick = { props.prepareMove(PlayerMove(MoveType.DISCARD, card.name)) },
        )
    }
}

private fun cardPlayabilityInfo(playability: CardPlayability) = when (playability.isPlayable) {
    true -> priceText(-playability.minPrice)
    false -> playability.playabilityLevel.message
}

private fun wonderBuildabilityInfo(buildability: WonderBuildability) = when (buildability.isBuildable) {
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
    val size = 1.rem
    goldIndicator(
        amount = amount,
        amountPosition = TokenCountPosition.OVER,
        imgSize = size,
        customCountStyle = {
            fontFamily = "sans-serif"
            fontSize = size * 0.8
        },
    ) {
        css {
            position = Position.absolute
            top = (-0.2).rem
            left = (-0.2).rem
        }
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

fun RBuilder.handCards(
    turnInfo: PlayerTurnInfo,
    preparedMove: PlayerMove?,
    prepareMove: (PlayerMove) -> Unit,
    startTransactionsSelection: (TransactionSelectorState) -> Unit,
) {
    child(HandComponent::class) {
        attrs {
            this.turnInfo = turnInfo
            this.preparedMove = preparedMove
            this.prepareMove = prepareMove
            this.startTransactionsSelection = startTransactionsSelection
        }
    }
}

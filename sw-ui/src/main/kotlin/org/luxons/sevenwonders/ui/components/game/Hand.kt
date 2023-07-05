package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.*
import blueprintjs.icons.*
import csstype.*
import emotion.react.*
import org.luxons.sevenwonders.client.*
import org.luxons.sevenwonders.model.*
import org.luxons.sevenwonders.model.boards.*
import org.luxons.sevenwonders.model.cards.*
import org.luxons.sevenwonders.model.resources.*
import org.luxons.sevenwonders.model.wonders.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.ReactHTML.div
import web.cssom.*
import web.cssom.Position
import kotlin.math.*

fun ChildrenBuilder.handCards(
    game: GameState,
    prepareMove: (PlayerMove) -> Unit,
    startTransactionsSelection: (TransactionSelectorState) -> Unit,
) {
    HandCards {
        this.action = game.action
        this.ownBoard = game.getOwnBoard()
        this.preparedMove = game.currentPreparedMove
        this.prepareMove = { moveType: MoveType, card: HandCard, transactionOptions: ResourceTransactionOptions ->
            when (transactionOptions.size) {
                1 -> prepareMove(PlayerMove(moveType, card.name, transactionOptions.single()))
                else -> startTransactionsSelection(TransactionSelectorState(moveType, card, transactionOptions))
            }
        }
    }
}

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

external interface HandCardsProps : Props {
    var action: TurnAction
    var ownBoard: Board
    var preparedMove: PlayerMove?
    var prepareMove: (MoveType, HandCard, ResourceTransactionOptions) -> Unit
}

private val HandCards = FC<HandCardsProps>("HandCards") { props ->
    val hand = props.action.cardsToPlay() ?: return@FC
    div {
        css {
            handStyle()
        }
        hand.filter { it.name != props.preparedMove?.cardName }.forEachIndexed { index, c ->
            HandCard {
                card = c
                action = props.action
                ownBoard = props.ownBoard
                prepareMove = props.prepareMove
                key = index.toString()
            }
        }
    }
}

private fun TurnAction.cardsToPlay(): List<HandCard>? = when (this) {
    is TurnAction.PlayFromHand -> hand
    is TurnAction.PlayFromDiscarded -> discardedCards
    is TurnAction.PickNeighbourGuild -> neighbourGuildCards
    is TurnAction.SayReady,
    is TurnAction.Wait,
    is TurnAction.WatchScore -> null
}

private external interface HandCardProps : Props {
    var card: HandCard
    var action: TurnAction
    var ownBoard: Board
    var prepareMove: (MoveType, HandCard, ResourceTransactionOptions) -> Unit
}

private val HandCard = FC<HandCardProps>("HandCard") { props ->
    div {
        css(ClassName("hand-card")) {
            alignItems = AlignItems.flexEnd
            display = Display.grid
            margin = Margin(all = 0.2.rem)
        }
        CardImage {
            css {
                val isPlayable = props.card.playability.isPlayable || props.ownBoard.canPlayAnyCardForFree
                handCardImgStyle(isPlayable)
            }
            this.card = props.card
        }
        actionButtons(props.card, props.action, props.ownBoard, props.prepareMove)
    }
}

private fun ChildrenBuilder.actionButtons(
    card: HandCard,
    action: TurnAction,
    ownBoard: Board,
    prepareMove: (MoveType, HandCard, ResourceTransactionOptions) -> Unit,
) {
    div {
        css {
            justifyContent = JustifyContent.center
            alignItems = AlignItems.flexEnd
            display = None.none
            gridRow = integer(1)
            gridColumn = integer(1)

            ancestorHover(".hand-card") {
                display = Display.flex
            }
        }
        BpButtonGroup {
            when (action) {
                is TurnAction.PlayFromHand -> {
                    playCardButton(card, HandAction.PLAY, prepareMove)
                    if (ownBoard.canPlayAnyCardForFree) {
                        playCardButton(card.copy(playability = CardPlayability.SPECIAL_FREE), HandAction.PLAY_FREE, prepareMove)
                    }
                }
                is TurnAction.PlayFromDiscarded -> playCardButton(card, HandAction.PLAY_FREE_DISCARDED, prepareMove)
                is TurnAction.PickNeighbourGuild -> playCardButton(card, HandAction.COPY_GUILD, prepareMove)
                is TurnAction.SayReady,
                is TurnAction.Wait,
                is TurnAction.WatchScore -> error("unsupported action in hand card: $action")
            }

            if (action.allowsBuildingWonder()) {
                upgradeWonderButton(card, ownBoard.wonder.buildability, prepareMove)
            }
            if (action.allowsDiscarding()) {
                discardButton(card, prepareMove)
            }
        }
    }
}

private fun ChildrenBuilder.playCardButton(
    card: HandCard,
    handAction: HandAction,
    prepareMove: (MoveType, HandCard, ResourceTransactionOptions) -> Unit,
) {
    BpButton {
        title = "${handAction.buttonTitle} (${cardPlayabilityInfo(card.playability)})"
        large = true
        intent = Intent.SUCCESS
        disabled = !card.playability.isPlayable
        onClick = { prepareMove(handAction.moveType, card, card.playability.transactionOptions) }

        BpIcon { icon = handAction.icon }

        if (card.playability.isPlayable && !card.playability.isFree) {
            priceInfo(card.playability.minPrice)
        }
    }
}

private fun ChildrenBuilder.upgradeWonderButton(
    card: HandCard,
    wonderBuildability: WonderBuildability,
    prepareMove: (MoveType, HandCard, ResourceTransactionOptions) -> Unit,
) {
    BpButton {
        title = "UPGRADE WONDER (${wonderBuildabilityInfo(wonderBuildability)})"
        large = true
        intent = Intent.PRIMARY
        disabled = !wonderBuildability.isBuildable
        onClick = { prepareMove(MoveType.UPGRADE_WONDER, card, wonderBuildability.transactionsOptions) }

        BpIcon { icon = IconNames.KEY_SHIFT }
        if (wonderBuildability.isBuildable && !wonderBuildability.isFree) {
            priceInfo(wonderBuildability.minPrice)
        }
    }
}

private fun ChildrenBuilder.discardButton(card: HandCard, prepareMove: (MoveType, HandCard, ResourceTransactionOptions) -> Unit) {
    BpButton {
        title = "DISCARD (+3 coins)" // TODO remove hardcoded value
        large = true
        intent = Intent.DANGER
        icon = IconNames.CROSS
        onClick = { prepareMove(MoveType.DISCARD, card, singleOptionNoTransactionNeeded()) }
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

private fun ChildrenBuilder.priceInfo(amount: Int) {
    goldIndicator(
        amount = amount,
        amountPosition = TokenCountPosition.OVER,
        imgSize = 1.rem,
        customCountStyle = {
            fontFamily = FontFamily.sansSerif
            fontSize = 0.8.rem
        },
    ) {
        css {
            position = Position.absolute
            top = (-0.2).rem
            left = (-0.2).rem
        }
    }
}

private fun PropertiesBuilder.handStyle() {
    alignItems = AlignItems.center
    bottom = 0.px
    display = Display.flex
    height = 345.px
    left = 50.pct
    maxHeight = 25.vw
    position = Position.absolute
    transform  = translate(tx = (-50).pct, ty = 65.pct)
    transition = Transition(TransitionProperty.all, duration = 0.5.s, timingFunction = TransitionTimingFunction.ease)
    zIndex = integer(30)

    hover {
        bottom = 1.rem
        zIndex = integer(60)
        transform = translate(tx = (-50).pct, ty = 0.pct)
    }
}

private fun PropertiesBuilder.handCardImgStyle(isPlayable: Boolean) {
    gridRow = integer(1)
    gridColumn = integer(1)
    maxWidth = 13.vw
    maxHeight = 60.vh
    transition = Transition(TransitionProperty.all, duration = 0.1.s, timingFunction = TransitionTimingFunction.ease)
    width = 11.rem

    ancestorHover(".hand-card") {
        boxShadow = BoxShadow(offsetX = 0.px, offsetY = 10.px, blurRadius = 40.px, color = NamedColor.black)
        width = 14.rem
        maxWidth = 15.vw
        maxHeight = 90.vh
    }

    if (!isPlayable) {
        filter = grayscale(50.pct) + contrast(50.pct)
    }
}

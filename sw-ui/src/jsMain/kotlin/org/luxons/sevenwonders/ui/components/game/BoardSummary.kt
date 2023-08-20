package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.*
import csstype.*
import emotion.css.*
import emotion.react.*
import org.luxons.sevenwonders.model.api.*
import org.luxons.sevenwonders.model.boards.*
import org.luxons.sevenwonders.ui.components.gameBrowser.*
import org.luxons.sevenwonders.ui.utils.*
import react.*
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.hr
import web.cssom.*

enum class BoardSummarySide(
    val tokenCountPosition: TokenCountPosition,
    val alignment: AlignItems,
    val popoverPosition: PopoverPosition,
) {
    LEFT(TokenCountPosition.RIGHT, AlignItems.flexStart, PopoverPosition.RIGHT),
    TOP(TokenCountPosition.OVER, AlignItems.flexStart, PopoverPosition.BOTTOM),
    RIGHT(TokenCountPosition.LEFT, AlignItems.flexEnd, PopoverPosition.LEFT),
    BOTTOM(TokenCountPosition.OVER, AlignItems.flexStart, PopoverPosition.TOP),
}

external interface BoardSummaryWithPopoverProps : PropsWithClassName {
    var player: PlayerDTO
    var board: Board
    var side: BoardSummarySide
}

val BoardSummaryWithPopover = FC<BoardSummaryWithPopoverProps>("BoardSummaryWithPopover") { props ->
    BpPopover {
        content = BoardComponent.create {
            className = GameStyles.fullBoardPreview
            board = props.board
        }
        position = props.side.popoverPosition
        interactionKind = PopoverInteractionKind.HOVER
        popoverClassName = ClassName {
            val bgColor = GameStyles.sandBgColor.withAlpha(0.7)
            backgroundColor = bgColor
            borderRadius = 0.5.rem
            padding = Padding(all = 0.5.rem)

            children(".bp4-popover-content") {
                background = None.none // overrides default white background
            }
            descendants(".bp4-popover-arrow-fill") {
                set(CustomPropertyName("fill"), bgColor.toString()) // overrides default white arrow
            }
            descendants(".bp4-popover-arrow::before") {
                // The popover arrow is implemented with a simple square rotated 45 degrees (like a rhombus).
                // Since we use a semi-transparent background, we can see the box shadow of the rest of the arrow through
                // the popover, and thus we see the square. This boxShadow(transparent) is to avoid that.
                boxShadow = BoxShadow(0.px, 0.px, 0.px, 0.px, NamedColor.transparent)
            }
        }.toString()

        BoardSummary {
            this.className = props.className
            this.player = props.player
            this.board = props.board
            this.side = props.side
        }
    }
}

external interface BoardSummaryProps : PropsWithClassName {
    var player: PlayerDTO
    var board: Board
    var side: BoardSummarySide
    var showPreparationStatus: Boolean?
}

val BoardSummary = FC<BoardSummaryProps>("BoardSummary") { props ->
    div {
        css(props.className) {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = props.side.alignment
            padding = Padding(all = 0.5.rem)
            backgroundColor = NamedColor.palegoldenrod.withAlpha(0.5)
            zIndex = integer(50) // above table cards

            hover {
                backgroundColor = NamedColor.palegoldenrod
            }
        }

        val showPreparationStatus = props.showPreparationStatus ?: true
        topBar(props.player, props.side, showPreparationStatus)
        hr {
            css {
                margin = Margin(vertical = 0.5.rem, horizontal = 0.rem)
                width = 100.pct
            }
        }
        bottomBar(props.side, props.board, props.player, showPreparationStatus)
    }
}

private fun ChildrenBuilder.topBar(player: PlayerDTO, side: BoardSummarySide, showPreparationStatus: Boolean) {
    val playerIconSize = 25
    if (showPreparationStatus && side == BoardSummarySide.TOP) {
        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.spaceBetween
                width = 100.pct
            }
            PlayerInfo {
                this.player = player
                this.iconSize = playerIconSize
            }
            PlayerPreparedCard {
                this.playerDisplayName = player.displayName
                this.username = player.username
            }
        }
    } else {
        PlayerInfo {
            this.player = player
            this.iconSize = playerIconSize
        }
    }
}

private fun ChildrenBuilder.bottomBar(side: BoardSummarySide, board: Board, player: PlayerDTO, showPreparationStatus: Boolean) {
    div {
        css {
            display = Display.flex
            flexDirection = if (side == BoardSummarySide.TOP || side == BoardSummarySide.BOTTOM) FlexDirection.row else FlexDirection.column
            alignItems = side.alignment
            if (side != BoardSummarySide.TOP) {
                width = 100.pct
            }
        }
        val tokenSize = 2.rem
        generalCounts(board, tokenSize, side.tokenCountPosition)
        BpDivider()
        scienceTokens(board, tokenSize, side.tokenCountPosition)
        if (showPreparationStatus && side != BoardSummarySide.TOP) {
            BpDivider()
            div {
                css {
                    width = 100.pct
                    alignItems = AlignItems.center
                    display = Display.flex
                    flexDirection = FlexDirection.column
                }
                PlayerPreparedCard {
                    this.playerDisplayName = player.displayName
                    this.username = player.username
                }
            }
        }
    }
}

private fun ChildrenBuilder.generalCounts(
    board: Board,
    tokenSize: Length,
    countPosition: TokenCountPosition,
) {
    goldIndicator(amount = board.gold, imgSize = tokenSize, amountPosition = countPosition)
    tokenWithCount(
        tokenName = "laurel-blue",
        count = board.bluePoints,
        imgSize = tokenSize,
        countPosition = countPosition,
        brightText = countPosition == TokenCountPosition.OVER,
    )
    tokenWithCount(
        tokenName = "military/shield",
        count = board.military.nbShields,
        imgSize = tokenSize,
        countPosition = countPosition,
        brightText = countPosition == TokenCountPosition.OVER,
    )
}

private fun ChildrenBuilder.scienceTokens(
    board: Board,
    tokenSize: Length,
    sciencePosition: TokenCountPosition,
) {
    tokenWithCount(
        tokenName = "science/compass",
        count = board.science.nbCompasses,
        imgSize = tokenSize,
        countPosition = sciencePosition,
        brightText = sciencePosition == TokenCountPosition.OVER,
    )
    tokenWithCount(
        tokenName = "science/cog",
        count = board.science.nbWheels,
        imgSize = tokenSize,
        countPosition = sciencePosition,
        brightText = sciencePosition == TokenCountPosition.OVER,
    )
    tokenWithCount(
        tokenName = "science/tablet",
        count = board.science.nbTablets,
        imgSize = tokenSize,
        countPosition = sciencePosition,
        brightText = sciencePosition == TokenCountPosition.OVER,
    )
}

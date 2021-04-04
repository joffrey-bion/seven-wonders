package org.luxons.sevenwonders.ui.components.game

import blueprintjs.core.PopoverPosition
import blueprintjs.core.bpDivider
import blueprintjs.core.bpPopover
import kotlinx.css.*
import kotlinx.html.DIV
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.boards.Board
import org.luxons.sevenwonders.ui.components.gameBrowser.playerInfo
import react.RBuilder
import react.ReactElement
import react.buildElement
import styled.*

enum class BoardSummarySide(
    val tokenCountPosition: TokenCountPosition,
    val alignment: Align,
    val popoverPosition: PopoverPosition,
) {
    LEFT(TokenCountPosition.RIGHT, Align.flexStart, PopoverPosition.RIGHT),
    TOP(TokenCountPosition.OVER, Align.flexStart, PopoverPosition.BOTTOM),
    RIGHT(TokenCountPosition.LEFT, Align.flexEnd, PopoverPosition.LEFT),
    BOTTOM(TokenCountPosition.OVER, Align.flexStart, PopoverPosition.TOP),
}

fun RBuilder.boardSummaryWithPopover(
    player: PlayerDTO,
    board: Board,
    boardSummarySide: BoardSummarySide,
    block: StyledDOMBuilder<DIV>.() -> Unit = {},
) {
    val popoverClass = GameStyles.getClassName { it::fullBoardPreviewPopover }
    bpPopover(
        content = createFullBoardPreview(board),
        position = boardSummarySide.popoverPosition,
        popoverClassName = popoverClass,
    ) {
        boardSummary(
            player = player,
            board = board,
            side = boardSummarySide,
            block = block,
        )
    }
}

private fun createFullBoardPreview(board: Board): ReactElement = buildElement {
    boardComponent(board = board) {
        css {
            +GameStyles.fullBoardPreview
        }
    }
}

fun RBuilder.boardSummary(
    player: PlayerDTO,
    board: Board,
    side: BoardSummarySide,
    showPreparationStatus: Boolean = true,
    block: StyledDOMBuilder<DIV>.() -> Unit = {},
) {
    styledDiv {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = side.alignment
            padding(all = 0.5.rem)
            backgroundColor = Color.paleGoldenrod.withAlpha(0.5)
            zIndex = 50 // above table cards

            hover {
                backgroundColor = Color.paleGoldenrod
            }
        }

        topBar(player, side, showPreparationStatus)
        styledHr {
            css {
                margin(vertical = 0.5.rem)
                width = 100.pct
            }
        }
        bottomBar(side, board, player, showPreparationStatus)
        block()
    }
}

private fun RBuilder.topBar(player: PlayerDTO, side: BoardSummarySide, showPreparationStatus: Boolean) {
    val playerIconSize = 25
    if (showPreparationStatus && side == BoardSummarySide.TOP) {
        styledDiv {
            css {
                display = Display.flex
                flexDirection = FlexDirection.row
                justifyContent = JustifyContent.spaceBetween
                width = 100.pct
            }
            playerInfo(player, iconSize = playerIconSize)
            playerPreparedCard(player)
        }
    } else {
        playerInfo(player, iconSize = playerIconSize)
    }
}

private fun RBuilder.bottomBar(side: BoardSummarySide, board: Board, player: PlayerDTO, showPreparationStatus: Boolean) {
    styledDiv {
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
        bpDivider()
        scienceTokens(board, tokenSize, side.tokenCountPosition)
        if (showPreparationStatus && side != BoardSummarySide.TOP) {
            bpDivider()
            styledDiv {
                css {
                    width = 100.pct
                    alignItems = Align.center
                    display = Display.flex
                    flexDirection = FlexDirection.column
                }
                playerPreparedCard(player)
            }
        }
    }
}

private fun StyledDOMBuilder<DIV>.generalCounts(
    board: Board,
    tokenSize: LinearDimension,
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

private fun RBuilder.scienceTokens(
    board: Board,
    tokenSize: LinearDimension,
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

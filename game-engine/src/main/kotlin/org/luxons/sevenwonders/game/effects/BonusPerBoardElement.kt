package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.boards.BoardElementType
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition
import org.luxons.sevenwonders.game.cards.Color

data class BonusPerBoardElement (
    val boards: List<RelativeBoardPosition>,
    val type: BoardElementType,
    val gold: Int = 0,
    val points: Int = 0,
    // only relevant if type=CARD
    val colors: List<Color>? = null
) : Effect {

    override fun apply(table: Table, playerIndex: Int) {
        val goldGain = gold * computeNbOfMatchingElementsIn(table, playerIndex)
        val board = table.getBoard(playerIndex)
        board.addGold(goldGain)
    }

    override fun computePoints(table: Table, playerIndex: Int): Int =
        points * computeNbOfMatchingElementsIn(table, playerIndex)

    private fun computeNbOfMatchingElementsIn(table: Table, playerIndex: Int): Int = boards
        .map { pos -> table.getBoard(playerIndex, pos) }
        .map(::computeNbOfMatchingElementsIn)
        .sum()

    private fun computeNbOfMatchingElementsIn(board: Board): Int = type.getElementCount(board, colors)
}

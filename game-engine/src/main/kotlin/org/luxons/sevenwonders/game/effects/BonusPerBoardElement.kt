package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition
import org.luxons.sevenwonders.game.cards.Color

enum class BoardElementType {
    CARD,
    BUILT_WONDER_STAGES,
    DEFEAT_TOKEN
}

data class BonusPerBoardElement (
    val boards: List<RelativeBoardPosition>,
    val type: BoardElementType,
    val gold: Int = 0,
    val points: Int = 0,
    val colors: List<Color>? = null // only relevant if type=CARD
) : Effect {

    override fun apply(table: Table, playerIndex: Int) {
        val goldGain = gold * nbMatchingElementsIn(table, playerIndex)
        table.getBoard(playerIndex).addGold(goldGain)
    }

    override fun computePoints(table: Table, playerIndex: Int): Int = points * nbMatchingElementsIn(table, playerIndex)

    private fun nbMatchingElementsIn(table: Table, playerIndex: Int): Int = boards
        .map { pos -> table.getBoard(playerIndex, pos) }
        .map(::nbMatchingElementsIn)
        .sum()

    private fun nbMatchingElementsIn(board: Board): Int = when (type) {
            BoardElementType.CARD -> board.getNbCardsOfColor(colors!!)
            BoardElementType.BUILT_WONDER_STAGES -> board.wonder.nbBuiltStages
            BoardElementType.DEFEAT_TOKEN -> board.military.nbDefeatTokens
    }
}

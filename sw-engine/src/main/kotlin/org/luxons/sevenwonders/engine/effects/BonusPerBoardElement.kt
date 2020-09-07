package org.luxons.sevenwonders.engine.effects

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.cards.Color

enum class BoardElementType {
    CARD,
    BUILT_WONDER_STAGES,
    DEFEAT_TOKEN,
}

internal data class BonusPerBoardElement(
    val boards: List<RelativeBoardPosition>,
    val type: BoardElementType,
    val gold: Int = 0,
    val points: Int = 0,
    val colors: List<Color>? = null, // only relevant if type=CARD
) : Effect {

    override fun applyTo(player: Player) = player.board.addGold(gold * nbMatchingElementsFor(player))

    override fun computePoints(player: Player): Int = points * nbMatchingElementsFor(player)

    private fun nbMatchingElementsFor(player: Player): Int = boards.sumBy { nbMatchingElementsIn(player.getBoard(it)) }

    private fun nbMatchingElementsIn(board: Board): Int = when (type) {
        BoardElementType.CARD -> board.getNbCardsOfColor(colors!!)
        BoardElementType.BUILT_WONDER_STAGES -> board.wonder.nbBuiltStages
        BoardElementType.DEFEAT_TOKEN -> board.military.nbDefeatTokens
    }
}

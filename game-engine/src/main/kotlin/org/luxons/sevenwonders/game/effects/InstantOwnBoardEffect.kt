package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board

abstract class InstantOwnBoardEffect : Effect {

    override fun apply(table: Table, playerIndex: Int) = apply(table.getBoard(playerIndex))

    protected abstract fun apply(board: Board)

    // InstantEffects are only important when applied to the board, they don't give extra points in the end
    override fun computePoints(table: Table, playerIndex: Int): Int = 0
}

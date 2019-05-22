package org.luxons.sevenwonders.engine.effects

import org.luxons.sevenwonders.engine.boards.Board

internal data class GoldIncrease(val amount: Int) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) = board.addGold(amount)
}

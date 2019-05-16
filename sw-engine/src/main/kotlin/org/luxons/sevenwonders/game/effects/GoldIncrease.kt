package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.boards.Board

internal data class GoldIncrease(val amount: Int) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) = board.addGold(amount)
}

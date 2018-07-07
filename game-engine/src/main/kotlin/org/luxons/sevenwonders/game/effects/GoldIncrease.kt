package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.boards.Board

data class GoldIncrease(val amount: Int) : InstantOwnBoardEffect() {

    public override fun apply(board: Board) = board.addGold(amount)
}

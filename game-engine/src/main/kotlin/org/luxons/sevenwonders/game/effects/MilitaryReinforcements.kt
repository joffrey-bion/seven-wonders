package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.boards.Board

data class MilitaryReinforcements(val count: Int) : InstantOwnBoardEffect() {

    public override fun apply(board: Board) = board.military.addShields(count)
}

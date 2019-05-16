package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.boards.Board

internal data class MilitaryReinforcements(val count: Int) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) = board.military.addShields(count)
}

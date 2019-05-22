package org.luxons.sevenwonders.engine.effects

import org.luxons.sevenwonders.engine.boards.Board

internal data class MilitaryReinforcements(val count: Int) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) = board.military.addShields(count)
}

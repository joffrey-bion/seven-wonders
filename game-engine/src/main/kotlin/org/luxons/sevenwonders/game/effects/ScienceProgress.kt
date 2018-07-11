package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.boards.Science

class ScienceProgress(val science: Science) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) = board.science.addAll(science)
}

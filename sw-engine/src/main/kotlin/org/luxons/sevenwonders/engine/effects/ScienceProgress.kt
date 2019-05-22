package org.luxons.sevenwonders.engine.effects

import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.boards.Science

internal class ScienceProgress(val science: Science) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) = board.science.addAll(science)
}

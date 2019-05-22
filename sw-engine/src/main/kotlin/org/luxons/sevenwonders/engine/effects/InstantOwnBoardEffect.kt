package org.luxons.sevenwonders.engine.effects

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.boards.Board

internal abstract class InstantOwnBoardEffect : Effect {

    override fun applyTo(player: Player) = applyTo(player.board)

    protected abstract fun applyTo(board: Board)

    // InstantEffects are only important when applied to the board, they don't give extra points in the end
    override fun computePoints(player: Player): Int = 0
}

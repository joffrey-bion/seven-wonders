package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Board

abstract class InstantOwnBoardEffect : Effect {

    override fun applyTo(player: Player) = applyTo(player.board)

    protected abstract fun applyTo(board: Board)

    // InstantEffects are only important when applied to the board, they don't give extra points in the end
    override fun computePoints(player: Player): Int = 0
}

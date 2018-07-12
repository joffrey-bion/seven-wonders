package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.resources.Production

internal data class ProductionIncrease(val production: Production, val isSellable: Boolean) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) {
        board.production.addAll(production)
        if (isSellable) {
            board.publicProduction.addAll(production)
        }
    }
}

package org.luxons.sevenwonders.engine.effects

import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.resources.Production

internal data class ProductionIncrease(val production: Production, val isSellable: Boolean) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) {
        board.production.addAll(production)
        if (isSellable) {
            board.publicProduction.addAll(production)
        }
    }
}

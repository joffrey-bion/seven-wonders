package org.luxons.sevenwonders.engine.effects

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.resources.Production

@Serializable
internal data class ProductionIncrease(
    val resources: Production,
    val isSellable: Boolean
) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) {
        board.production.addAll(resources)
        if (isSellable) {
            board.publicProduction.addAll(resources)
        }
    }
}

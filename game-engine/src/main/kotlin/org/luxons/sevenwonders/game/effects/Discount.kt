package org.luxons.sevenwonders.game.effects

import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.resources.Provider
import org.luxons.sevenwonders.game.resources.ResourceType

data class Discount(
    val resourceTypes: List<ResourceType> = emptyList(),
    val providers: List<Provider> = emptyList(),
    val discountedPrice: Int = 1
) : InstantOwnBoardEffect() {

    public override fun applyTo(board: Board) {
        val rules = board.tradingRules
        for (type in resourceTypes) {
            providers.forEach { rules.setCost(type, it, discountedPrice) }
        }
    }
}

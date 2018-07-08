package org.luxons.sevenwonders.game.wonders

import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.resources.ResourceTransactions

class WonderStage(val requirements: Requirements, val effects: List<Effect>) {

    var cardBack: CardBack? = null
        private set

    val isBuilt: Boolean
        get() = cardBack != null

    fun isBuildable(table: Table, playerIndex: Int, boughtResources: ResourceTransactions): Boolean {
        val board = table.getBoard(playerIndex)
        return requirements.areMetWithHelpBy(board, boughtResources)
    }

    internal fun build(cardBack: CardBack) {
        this.cardBack = cardBack
    }

    internal fun activate(table: Table, playerIndex: Int, boughtResources: ResourceTransactions) {
        effects.forEach { e -> e.apply(table, playerIndex) }
        requirements.pay(table, playerIndex, boughtResources)
    }
}

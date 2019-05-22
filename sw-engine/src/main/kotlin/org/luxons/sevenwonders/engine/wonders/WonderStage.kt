package org.luxons.sevenwonders.engine.wonders

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.cards.Requirements
import org.luxons.sevenwonders.engine.effects.Effect
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.resources.ResourceTransactions

internal class WonderStage(
    val requirements: Requirements,
    val effects: List<Effect>
) {
    var cardBack: CardBack? = null
        private set

    val isBuilt: Boolean
        get() = cardBack != null

    fun isBuildable(board: Board, boughtResources: ResourceTransactions): Boolean =
        requirements.areMetWithHelpBy(board, boughtResources)

    fun placeCard(cardBack: CardBack) {
        this.cardBack = cardBack
    }

    fun activate(player: Player, boughtResources: ResourceTransactions) {
        effects.forEach { it.applyTo(player) }
        requirements.pay(player, boughtResources)
    }
}

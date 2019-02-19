package org.luxons.sevenwonders.game.wonders

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.resources.ResourceTransactions

class WonderStage internal constructor(val requirements: Requirements, internal val effects: List<Effect>) {

    var cardBack: CardBack? = null
        private set

    val isBuilt: Boolean
        get() = cardBack != null

    internal fun isBuildable(board: Board, boughtResources: ResourceTransactions): Boolean {
        return requirements.areMetWithHelpBy(board, boughtResources)
    }

    internal fun placeCard(cardBack: CardBack) {
        this.cardBack = cardBack
    }

    internal fun activate(player: Player, boughtResources: ResourceTransactions) {
        effects.forEach { it.applyTo(player) }
        requirements.pay(player, boughtResources)
    }
}

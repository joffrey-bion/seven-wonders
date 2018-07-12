package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.cards.Card

/**
 * A card with contextual information relative to the hand it is sitting in. The extra information is especially useful
 * because it frees the client from a painful business logic implementation.
 */
class HandCard internal constructor(val card: Card, player: Player) {
    val isChainable: Boolean = card.isChainableOn(player.board)
    val isFree: Boolean = card.isFreeFor(player.board)
    val isPlayable: Boolean = card.isPlayableBy(player)
}

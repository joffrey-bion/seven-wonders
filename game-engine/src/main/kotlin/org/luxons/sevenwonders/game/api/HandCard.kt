package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.cards.Card

/**
 * A card with contextual information relative to the hand it is sitting in. The extra information is especially useful
 * because it frees the client from a painful business logic implementation.
 */
class HandCard(val card: Card, table: Table, playerIndex: Int) {

    val isChainable: Boolean
    val isFree: Boolean
    val isPlayable: Boolean = card.isPlayable(table, playerIndex)

    init {
        val board = table.getBoard(playerIndex)
        this.isChainable = card.isChainableOn(board)
        this.isFree = card.isFreeFor(board)
    }
}

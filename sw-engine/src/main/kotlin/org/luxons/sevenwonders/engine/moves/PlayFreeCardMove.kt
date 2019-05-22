package org.luxons.sevenwonders.engine.moves

import org.luxons.sevenwonders.engine.PlayerContext
import org.luxons.sevenwonders.engine.Settings
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.model.PlayerMove

internal class PlayFreeCardMove(move: PlayerMove, card: Card, playerContext: PlayerContext) :
    CardFromHandMove(move, card, playerContext) {

    init {
        val board = playerContext.board
        if (!board.canPlayFreeCard(playerContext.currentAge)) {
            throw InvalidMoveException(this, "no free card available for the current age ${playerContext.currentAge}")
        }
    }

    override fun place(discardedCards: MutableList<Card>, settings: Settings) = playerContext.board.addCard(card)

    override fun activate(discardedCards: List<Card>, settings: Settings) {
        // only apply effects, without paying the cost
        card.effects.forEach { it.applyTo(playerContext) }
        playerContext.board.consumeFreeCard(playerContext.currentAge)
    }
}

package org.luxons.sevenwonders.engine.moves

import org.luxons.sevenwonders.engine.PlayerContext
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.Settings

internal class DiscardMove(move: PlayerMove, card: Card, player: PlayerContext) :
    CardFromHandMove(move, card, player) {

    override fun place(discardedCards: MutableList<Card>, settings: Settings) {
        discardedCards.add(card)
    }

    override fun activate(discardedCards: List<Card>, settings: Settings) {
        playerContext.board.addGold(settings.discardedCardGold)
    }
}

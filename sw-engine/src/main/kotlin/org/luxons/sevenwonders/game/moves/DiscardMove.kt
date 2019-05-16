package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.PlayerContext
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.cards.Card

internal class DiscardMove(move: PlayerMove, card: Card, player: PlayerContext) :
    CardFromHandMove(move, card, player) {

    override fun place(discardedCards: MutableList<Card>, settings: Settings) {
        discardedCards.add(card)
    }

    override fun activate(discardedCards: List<Card>, settings: Settings) {
        playerContext.board.addGold(settings.discardedCardGold)
    }
}

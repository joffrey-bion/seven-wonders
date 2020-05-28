package org.luxons.sevenwonders.engine.moves

import org.luxons.sevenwonders.engine.PlayerContext
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.effects.SpecialAbility
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.PlayerMove

internal class PlayFreeDiscardedCardMove(
    move: PlayerMove,
    card: Card,
    playerContext: PlayerContext,
    discardedCards: List<Card>
) : Move(move, card, playerContext) {

    init {
        val board = playerContext.board
        if (!board.hasSpecial(SpecialAbility.PLAY_DISCARDED)) {
            throw InvalidMoveException(this, "no special ability to play a discarded card")
        }
        if (card !in discardedCards) {
            throw InvalidMoveException(this, "Card $card is not among the discard cards")
        }
    }

    override fun place(discardedCards: MutableList<Card>, settings: Settings) {
        discardedCards.remove(card)
        playerContext.board.addCard(card)
    }

    override fun activate(discardedCards: List<Card>, settings: Settings) {
        // only apply effects, without paying the cost
        card.effects.forEach { it.applyTo(playerContext) }
        playerContext.board.removeSpecial(SpecialAbility.PLAY_DISCARDED)
    }
}

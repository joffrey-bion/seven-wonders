package org.luxons.sevenwonders.engine.moves

import org.luxons.sevenwonders.engine.PlayerContext
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.model.PlayerMove

internal abstract class CardFromHandMove(move: PlayerMove, card: Card, player: PlayerContext) :
    Move(move, card, player) {

    init {
        if (!player.hand.contains(card)) {
            throw InvalidMoveException(this, "card '${card.name}' not in hand")
        }
    }
}

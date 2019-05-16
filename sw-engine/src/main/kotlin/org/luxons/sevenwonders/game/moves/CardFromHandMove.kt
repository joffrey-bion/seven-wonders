package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.PlayerContext
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.cards.Card

internal abstract class CardFromHandMove(move: PlayerMove, card: Card, player: PlayerContext) :
    Move(move, card, player) {

    init {
        if (!player.hand.contains(card)) {
            throw InvalidMoveException(this, "card '${card.name}' not in hand")
        }
    }
}

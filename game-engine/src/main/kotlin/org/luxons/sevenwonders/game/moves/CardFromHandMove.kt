package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.cards.Card

abstract class CardFromHandMove internal constructor(playerIndex: Int, card: Card, move: PlayerMove) :
    Move(playerIndex, card, move) {

    @Throws(InvalidMoveException::class)
    override fun validate(table: Table, playerHand: List<Card>) {
        if (!playerHand.contains(card)) {
            throw InvalidMoveException("Player $playerIndex does not have the card '${card.name}' in his hand")
        }
    }
}

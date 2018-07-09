package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.cards.Card

class PlayFreeCardMove internal constructor(playerIndex: Int, card: Card, move: PlayerMove) :
    CardFromHandMove(playerIndex, card, move) {

    @Throws(InvalidMoveException::class)
    override fun validate(table: Table, playerHand: List<Card>) {
        super.validate(table, playerHand)
        val board = table.getBoard(playerIndex)
        if (!board.canPlayFreeCard(table.currentAge)) {
            throw InvalidMoveException(
                String.format("Player %d cannot play the card %s for free", playerIndex, card.name)
            )
        }
    }

    override fun place(table: Table, discardedCards: MutableList<Card>, settings: Settings) {
        val board = table.getBoard(playerIndex)
        board.addCard(card)
    }

    override fun activate(table: Table, discardedCards: List<Card>, settings: Settings) {
        // only apply effects, without paying the cost
        card.effects.forEach { e -> e.apply(table, playerIndex) }
        val board = table.getBoard(playerIndex)
        board.consumeFreeCard(table.currentAge)
    }
}

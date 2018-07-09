package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.cards.Card

class DiscardMove internal constructor(playerIndex: Int, card: Card, move: PlayerMove) :
    CardFromHandMove(playerIndex, card, move) {

    override fun place(table: Table, discardedCards: MutableList<Card>, settings: Settings) {
        discardedCards.add(card)
    }

    override fun activate(table: Table, discardedCards: List<Card>, settings: Settings) {
        val board = table.getBoard(playerIndex)
        board.addGold(settings.discardedCardGold)
    }
}

package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.cards.Card

class PlayCardMove internal constructor(playerIndex: Int, card: Card, move: PlayerMove) :
    CardFromHandMove(playerIndex, card, move) {

    @Throws(InvalidMoveException::class)
    override fun validate(table: Table, playerHand: List<Card>) {
        super.validate(table, playerHand)
        val board = table.getBoard(playerIndex)
        if (!card.isChainableOn(board) && !card.requirements.areMetWithHelpBy(board, transactions)) {
            throw InvalidMoveException("Player $playerIndex cannot play the card ${card.name} with the given resources")
        }
    }

    override fun place(table: Table, discardedCards: MutableList<Card>, settings: Settings) =
        table.getBoard(playerIndex).addCard(card)

    override fun activate(table: Table, discardedCards: List<Card>, settings: Settings) =
        card.applyTo(table, playerIndex, transactions)
}

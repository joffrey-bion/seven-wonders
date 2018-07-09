package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.cards.Card

class BuildWonderMove internal constructor(playerIndex: Int, card: Card, move: PlayerMove) :
    CardFromHandMove(playerIndex, card, move) {

    @Throws(InvalidMoveException::class)
    override fun validate(table: Table, playerHand: List<Card>) {
        super.validate(table, playerHand)
        val board = table.getBoard(playerIndex)
        if (!board.wonder.isNextStageBuildable(table, playerIndex, transactions)) {
            throw InvalidMoveException("Player $playerIndex cannot upgrade his wonder with the given resources")
        }
    }

    override fun place(table: Table, discardedCards: MutableList<Card>, settings: Settings) =
        table.getBoard(playerIndex).wonder.buildLevel(card.back)

    override fun activate(table: Table, discardedCards: List<Card>, settings: Settings) =
        table.getBoard(playerIndex).wonder.activateLastBuiltStage(table, playerIndex, transactions)
}

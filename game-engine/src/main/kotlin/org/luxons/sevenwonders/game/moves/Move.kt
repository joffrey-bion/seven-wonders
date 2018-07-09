package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.resources.ResourceTransactions

abstract class Move internal constructor(val playerIndex: Int, val card: Card, move: PlayerMove) {

    val type: MoveType = move.type

    val transactions: ResourceTransactions = ResourceTransactions(move.transactions)

    @Throws(InvalidMoveException::class)
    abstract fun validate(table: Table, playerHand: List<Card>)

    abstract fun place(table: Table, discardedCards: MutableList<Card>, settings: Settings)

    abstract fun activate(table: Table, discardedCards: List<Card>, settings: Settings)
}

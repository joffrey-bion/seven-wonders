package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.PlayerContext
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.resources.ResourceTransactions

abstract class Move internal constructor(
    move: PlayerMove,
    val card: Card,
    internal val playerContext: PlayerContext
) {
    val type: MoveType = move.type

    val transactions: ResourceTransactions = ResourceTransactions(move.transactions)

    abstract fun place(discardedCards: MutableList<Card>, settings: Settings)

    abstract fun activate(discardedCards: List<Card>, settings: Settings)
}

class InvalidMoveException internal constructor(move: Move, message: String) : IllegalArgumentException(
    "Player ${move.playerContext.index} cannot perform move ${move.type}: $message"
)

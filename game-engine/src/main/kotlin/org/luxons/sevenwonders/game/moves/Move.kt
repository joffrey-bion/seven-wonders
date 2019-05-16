package org.luxons.sevenwonders.game.moves

import org.luxons.sevenwonders.game.PlayerContext
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.PlayerMove
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.resources.ResourceTransactions

internal abstract class Move(
    val move: PlayerMove,
    val card: Card,
    val playerContext: PlayerContext
) {
    val type: MoveType = move.type

    val transactions: ResourceTransactions = move.transactions

    abstract fun place(discardedCards: MutableList<Card>, settings: Settings)

    abstract fun activate(discardedCards: List<Card>, settings: Settings)
}

class InvalidMoveException internal constructor(move: Move, message: String) : IllegalArgumentException(
    "Player ${move.playerContext.index} cannot perform move ${move.type}: $message"
)

internal fun MoveType.resolve(move: PlayerMove, card: Card, context: PlayerContext): Move = when (this) {
    MoveType.PLAY -> PlayCardMove(move, card, context)
    MoveType.PLAY_FREE -> PlayFreeCardMove(move, card, context)
    MoveType.UPGRADE_WONDER -> BuildWonderMove(move, card, context)
    MoveType.DISCARD -> DiscardMove(move, card, context)
    MoveType.COPY_GUILD -> CopyGuildMove(move, card, context)
}

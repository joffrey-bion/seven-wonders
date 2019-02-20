package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.cards.HandRotationDirection
import org.luxons.sevenwonders.game.data.Age
import org.luxons.sevenwonders.game.moves.Move
import org.luxons.sevenwonders.game.moves.MoveType
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.boards.Table as InternalTable

data class Table(
    val boards: List<Board>,
    val currentAge: Age,
    val handRotationDirection: HandRotationDirection,
    val lastPlayedMoves: List<PlayedMove>
) {
    val nbPlayers: Int = boards.size
}

internal fun InternalTable.toApiTable(): Table = Table(
    boards = boards.map { it.toApiBoard() },
    currentAge = currentAge,
    handRotationDirection = handRotationDirection,
    lastPlayedMoves = lastPlayedMoves.map { it.toPlayedMove() }
)

data class PlayedMove(
    val playerIndex: Int,
    val type: MoveType,
    val card: TableCard,
    val transactions: ResourceTransactions
)

internal fun Move.toPlayedMove(): PlayedMove = PlayedMove(
    playerIndex = playerContext.index,
    type = type,
    card = card.toTableCard(),
    transactions = transactions
)

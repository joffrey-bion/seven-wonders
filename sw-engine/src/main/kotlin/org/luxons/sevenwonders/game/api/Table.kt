package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.SimplePlayer
import org.luxons.sevenwonders.game.moves.Move
import org.luxons.sevenwonders.game.boards.Table as InternalTable

internal fun InternalTable.toApiTable(): Table = Table(
    boards = boards.mapIndexed { i, b -> b.toApiBoard(SimplePlayer(i, this), lastPlayedMoves.getOrNull(i)) },
    currentAge = currentAge,
    handRotationDirection = handRotationDirection,
    lastPlayedMoves = lastPlayedMoves.map { it.toPlayedMove() }
)

internal fun Move.toPlayedMove(): PlayedMove = PlayedMove(
    playerIndex = playerContext.index,
    type = type,
    card = card.toTableCard(this),
    transactions = transactions
)

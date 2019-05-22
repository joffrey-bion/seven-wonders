package org.luxons.sevenwonders.engine.converters

import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.moves.Move
import org.luxons.sevenwonders.model.ApiTable
import org.luxons.sevenwonders.model.PlayedMove

internal fun Table.toApiTable(): ApiTable = ApiTable(
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

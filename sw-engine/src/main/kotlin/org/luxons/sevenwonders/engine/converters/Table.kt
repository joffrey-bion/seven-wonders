package org.luxons.sevenwonders.engine.converters

import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.engine.moves.Move
import org.luxons.sevenwonders.model.PlayedMove
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.model.TableState

internal fun Table.toTableState(): TableState = TableState(
    boards = boards.mapIndexed { i, b -> b.toApiBoard(SimplePlayer(i, this), lastPlayedMoves.getOrNull(i), currentAge) },
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

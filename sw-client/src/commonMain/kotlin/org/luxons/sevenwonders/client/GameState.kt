package org.luxons.sevenwonders.client

import org.luxons.sevenwonders.model.Age
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.TurnAction
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.boards.Board
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.cards.HandRotationDirection

data class GameState(
    val gameId: Long,
    val playerIndex: Int,
    val currentAge: Age,
    val players: List<PlayerDTO>,
    val boards: List<Board>,
    val handRotationDirection: HandRotationDirection,
    val action: TurnAction,
    val preparedCardsByUsername: Map<String, CardBack?> = emptyMap(),
    val currentPreparedMove: PlayerMove? = null,
) {
    val currentPreparedCard: HandCard?
        get() {
            val hand = (action as? TurnAction.PlayFromHand)?.hand
            return hand?.firstOrNull { it.name == currentPreparedMove?.cardName }
        }

    val RelativeBoardPosition.absoluteIndex: Int
        get() = getIndexFrom(playerIndex, boards.size)
}

fun GameState.getOwnBoard(): Board = boards[playerIndex]

fun GameState.getBoard(position: RelativeBoardPosition): Board = boards[position.absoluteIndex]

fun GameState.getNonNeighbourBoards(): List<Board> {
    val nPlayers = boards.size
    if (nPlayers <= 3) {
        return emptyList()
    }
    val first = (playerIndex + 2) % nPlayers
    val last = (playerIndex - 2 + nPlayers) % nPlayers
    val range = if (first <= last) first..last else ((first..<nPlayers) + (0..last))
    return range.map { boards[it] }
}

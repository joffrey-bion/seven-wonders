package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.boards.Board
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.wonders.WonderBuildability

@Serializable
data class PlayerTurnInfo(
    val playerIndex: Int,
    val table: TableState,
    val action: TurnAction,
) {
    val currentAge: Int = table.currentAge
    val message: String = action.message
    val wonderBuildability: WonderBuildability = table.boards[playerIndex].wonder.buildability

    val RelativeBoardPosition.index: Int
        get() = getIndexFrom(playerIndex, table.boards.size)
}

fun PlayerTurnInfo.getOwnBoard(): Board = table.boards[playerIndex]

fun PlayerTurnInfo.getBoard(position: RelativeBoardPosition): Board = table.boards[position.index]

fun PlayerTurnInfo.getNonNeighbourBoards(): List<Board> {
    val nPlayers = table.boards.size
    if (nPlayers <= 3) {
        return emptyList()
    }
    val first = (playerIndex + 2) % nPlayers
    val last = (playerIndex - 2 + nPlayers) % nPlayers
    val range = if (first <= last) first..last else ((first until nPlayers) + (0..last))
    return range.map { table.boards[it] }
}

// TODO move to server code
fun Collection<PlayerTurnInfo>.hideHandsAndWaitForReadiness() = map { it.copy(action = TurnAction.SayReady()) }

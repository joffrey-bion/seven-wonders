package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.boards.ApiBoard
import org.luxons.sevenwonders.game.cards.HandRotationDirection
import org.luxons.sevenwonders.game.cards.TableCard
import org.luxons.sevenwonders.game.moves.MoveType
import org.luxons.sevenwonders.game.resources.ResourceTransactions

typealias Age = Int

data class ApiTable(
    val boards: List<ApiBoard>,
    val currentAge: Age,
    val handRotationDirection: HandRotationDirection,
    val lastPlayedMoves: List<PlayedMove>
) {
    val nbPlayers: Int = boards.size
}

data class PlayedMove(
    val playerIndex: Int,
    val type: MoveType,
    val card: TableCard,
    val transactions: ResourceTransactions
)

package org.luxons.sevenwonders.model

import org.luxons.sevenwonders.model.boards.ApiBoard
import org.luxons.sevenwonders.model.cards.HandRotationDirection

typealias Age = Int

data class ApiTable(
    val boards: List<ApiBoard>,
    val currentAge: Age,
    val handRotationDirection: HandRotationDirection,
    val lastPlayedMoves: List<PlayedMove>
) {
    val nbPlayers: Int = boards.size
}

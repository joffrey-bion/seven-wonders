package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.api.boards.ApiBoard
import org.luxons.sevenwonders.game.api.cards.HandRotationDirection

typealias Age = Int

data class ApiTable(
    val boards: List<ApiBoard>,
    val currentAge: Age,
    val handRotationDirection: HandRotationDirection,
    val lastPlayedMoves: List<PlayedMove>
) {
    val nbPlayers: Int = boards.size
}

package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.boards.Board
import org.luxons.sevenwonders.model.cards.HandRotationDirection

typealias Age = Int

@Serializable
data class GameState(
    val boards: List<Board>,
    val currentAge: Age,
    val handRotationDirection: HandRotationDirection,
    val lastPlayedMoves: List<PlayedMove>
) {
    val nbPlayers: Int = boards.size
}

package org.luxons.sevenwonders.game

import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition
import org.luxons.sevenwonders.game.cards.Card

internal interface Player {
    val index: Int
    val board: Board
    fun getBoard(relativePosition: RelativeBoardPosition): Board
}

internal data class SimplePlayer(
    override val index: Int,
    private val table: Table
): Player {
    override val board = table.getBoard(index)
    override fun getBoard(relativePosition: RelativeBoardPosition) = table.getBoard(index, relativePosition)
}

internal data class PlayerContext(
    override val index: Int,
    private val table: Table,
    val hand: List<Card>
) : Player by SimplePlayer(index, table) {
    val currentAge = table.currentAge
}

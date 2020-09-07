package org.luxons.sevenwonders.engine

import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition

internal interface Player {
    val index: Int
    val board: Board
    fun getBoard(relativePosition: RelativeBoardPosition): Board
}

internal data class SimplePlayer(
    override val index: Int,
    private val table: Table,
) : Player {
    override val board = table.getBoard(index)
    override fun getBoard(relativePosition: RelativeBoardPosition) = table.getBoard(index, relativePosition)
}

internal data class PlayerContext(
    override val index: Int,
    private val table: Table,
    val hand: List<Card>,
) : Player by SimplePlayer(index, table) {
    val currentAge = table.currentAge
}

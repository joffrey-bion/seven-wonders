package org.luxons.sevenwonders.game.cards

import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.resources.ResourceTransactions

data class CardBack(val image: String)

data class Card(
    val name: String,
    val color: Color,
    val requirements: Requirements,
    val effects: List<Effect>,
    val chainParent: String?,
    val chainChildren: List<String>,
    val image: String,
    val back: CardBack
) {
    private fun isAllowedOnBoard(board: Board): Boolean = !board.isPlayed(name) // cannot play twice the same card

    fun isFreeFor(board: Board): Boolean = isChainableOn(board) || isFreeWithoutChainingOn(board)

    fun isChainableOn(board: Board): Boolean =
        isAllowedOnBoard(board) && chainParent != null && board.isPlayed(chainParent)

    private fun isFreeWithoutChainingOn(board: Board) =
        isAllowedOnBoard(board) && requirements.areMetWithoutNeighboursBy(board) && requirements.gold == 0

    fun isPlayable(table: Table, playerIndex: Int): Boolean {
        val board = table.getBoard(playerIndex)
        return isAllowedOnBoard(board) && (isChainableOn(board) || requirements.areMetBy(table, playerIndex))
    }

    fun applyTo(table: Table, playerIndex: Int, transactions: ResourceTransactions) {
        val playerBoard = table.getBoard(playerIndex)
        if (!isChainableOn(playerBoard)) {
            requirements.pay(table, playerIndex, transactions)
        }
        effects.forEach { e -> e.apply(table, playerIndex) }
    }
}

enum class Color {
    BROWN,
    GREY,
    YELLOW,
    BLUE,
    GREEN,
    RED,
    PURPLE
}

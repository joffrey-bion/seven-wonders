package org.luxons.sevenwonders.game.cards

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.resources.ResourceTransactions

data class CardBack(val image: String)

data class Card internal constructor(
    val name: String,
    val color: Color,
    val requirements: Requirements,
    internal val effects: List<Effect>,
    val chainParent: String?,
    val chainChildren: List<String>,
    val image: String,
    val back: CardBack
) {
    private fun isAllowedOnBoard(board: Board): Boolean = !board.isPlayed(name) // cannot play twice the same card

    internal fun isFreeFor(board: Board): Boolean = isChainableOn(board) || isFreeWithoutChainingOn(board)

    internal fun isChainableOn(board: Board): Boolean =
        isAllowedOnBoard(board) && chainParent != null && board.isPlayed(chainParent)

    private fun isFreeWithoutChainingOn(board: Board) =
        isAllowedOnBoard(board) && requirements.areMetWithoutNeighboursBy(board) && requirements.gold == 0

    internal fun isPlayableBy(player: Player): Boolean {
        val board = player.board
        return isAllowedOnBoard(board) && (isChainableOn(board) || requirements.areMetBy(player))
    }

    internal fun applyTo(player: Player, transactions: ResourceTransactions) {
        if (!isChainableOn(player.board)) {
            requirements.pay(player, transactions)
        }
        effects.forEach { it.applyTo(player) }
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

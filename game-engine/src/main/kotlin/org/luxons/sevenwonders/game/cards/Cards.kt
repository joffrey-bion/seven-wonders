package org.luxons.sevenwonders.game.cards

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.noTransactions

data class CardBack(val image: String)

data class CardPlayability(
    val isPlayable: Boolean,
    val isFree: Boolean = false,
    val isChainable: Boolean = false,
    val minPrice: Int = Int.MAX_VALUE,
    val cheapestTransactions: Set<ResourceTransactions> = emptySet()
)

internal data class Card(
    val name: String,
    val color: Color,
    val requirements: Requirements,
    val effects: List<Effect>,
    val chainParent: String?,
    val chainChildren: List<String>,
    val image: String,
    val back: CardBack
) {
    fun computePlayabilityBy(player: Player): CardPlayability {
        if (!isAllowedOnBoard(player.board)) {
            return nonPlayable()
        }
        if (isParentOnBoard(player.board)) {
            return chainablePlayability()
        }
        return requirementsPlayability(player)
    }

    private fun nonPlayable() = CardPlayability(isPlayable = false)

    private fun chainablePlayability(): CardPlayability = CardPlayability(
        isPlayable = true,
        isFree = true,
        isChainable = true,
        minPrice = 0,
        cheapestTransactions = setOf(noTransactions())
    )

    private fun requirementsPlayability(player: Player): CardPlayability {
        val satisfaction = requirements.computeSatisfaction(player)
        return CardPlayability(
            isPlayable = satisfaction is RequirementsSatisfaction.Acceptable,
            isFree = satisfaction.minPrice == 0,
            isChainable = false,
            minPrice = satisfaction.minPrice,
            cheapestTransactions = satisfaction.cheapestTransactions
        )
    }

    fun isChainableOn(board: Board): Boolean = isAllowedOnBoard(board) && isParentOnBoard(board)

    private fun isAllowedOnBoard(board: Board): Boolean = !board.isPlayed(name) // cannot play twice the same card

    private fun isParentOnBoard(board: Board): Boolean = chainParent != null && board.isPlayed(chainParent)

    fun applyTo(player: Player, transactions: ResourceTransactions) {
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

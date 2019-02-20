package org.luxons.sevenwonders.game.cards

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.noTransactions

data class CardBack(val image: String)

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
    fun computePlayabilityBy(player: Player): CardPlayability = when {
        isAlreadyOnBoard(player.board) -> CardPlayability.incompatibleWithBoard() // cannot play twice the same card
        isParentOnBoard(player.board) -> CardPlayability.chainable()
        else -> CardPlayability.requirementDependent(requirements.computeSatisfaction(player))
    }

    fun isPlayableOnBoardWith(board: Board, transactions: ResourceTransactions) =
        isChainableOn(board) || requirements.areMetWithHelpBy(board, transactions)

    private fun isChainableOn(board: Board): Boolean = !isAlreadyOnBoard(board) && isParentOnBoard(board)

    private fun isAlreadyOnBoard(board: Board): Boolean = board.isPlayed(name)

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

data class CardPlayability(
    val isPlayable: Boolean,
    val isFree: Boolean = false,
    val isChainable: Boolean = false,
    val minPrice: Int = Int.MAX_VALUE,
    val cheapestTransactions: Set<ResourceTransactions> = emptySet(),
    val playabilityLevel: PlayabilityLevel
) {
    companion object {

        internal fun incompatibleWithBoard(): CardPlayability =
            CardPlayability(isPlayable = false, playabilityLevel = PlayabilityLevel.INCOMPATIBLE_WITH_BOARD)

        internal fun chainable(): CardPlayability = CardPlayability(
            isPlayable = true,
            isFree = true,
            isChainable = true,
            minPrice = 0,
            cheapestTransactions = setOf(noTransactions()),
            playabilityLevel = PlayabilityLevel.CHAINABLE
        )

        internal fun requirementDependent(satisfaction: RequirementsSatisfaction): CardPlayability = CardPlayability(
            isPlayable = satisfaction.satisfied,
            isFree = satisfaction.minPrice == 0,
            isChainable = false,
            minPrice = satisfaction.minPrice,
            cheapestTransactions = satisfaction.cheapestTransactions,
            playabilityLevel = satisfaction.level
        )
    }
}

package org.luxons.sevenwonders.engine.cards

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.effects.Effect
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.CardPlayability
import org.luxons.sevenwonders.model.cards.Color
import org.luxons.sevenwonders.model.cards.PlayabilityLevel
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.singleOptionNoTransactionNeeded

internal data class Card(
    val name: String,
    val color: Color,
    val requirements: Requirements,
    val effects: List<Effect>,
    val chainParent: String?,
    val chainChildren: List<String>,
    val image: String,
    val back: CardBack,
) {
    fun computePlayabilityBy(player: Player, forceSpecialFree: Boolean = false): CardPlayability = when {
        isAlreadyOnBoard(player.board) -> Playability.alreadyPlayed() // cannot play twice the same card
        forceSpecialFree -> Playability.specialFree()
        isParentOnBoard(player.board) -> Playability.chainable()
        else -> Playability.requirementDependent(requirements.assess(player))
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

private object Playability {

    fun alreadyPlayed(): CardPlayability = CardPlayability(
        isPlayable = false,
        playabilityLevel = PlayabilityLevel.ALREADY_PLAYED,
    )

    fun chainable(): CardPlayability = CardPlayability(
        isPlayable = true,
        isChainable = true,
        minPrice = 0,
        transactionOptions = singleOptionNoTransactionNeeded(),
        playabilityLevel = PlayabilityLevel.CHAINABLE,
    )

    fun requirementDependent(satisfaction: RequirementsSatisfaction): CardPlayability = CardPlayability(
        isPlayable = satisfaction.satisfied,
        isChainable = false,
        minPrice = satisfaction.minPrice,
        transactionOptions = satisfaction.transactionOptions,
        playabilityLevel = satisfaction.level,
    )

    fun specialFree(): CardPlayability = CardPlayability(
        isPlayable = true,
        isChainable = false,
        minPrice = 0,
        transactionOptions = singleOptionNoTransactionNeeded(),
        playabilityLevel = PlayabilityLevel.SPECIAL_FREE,
    )
}

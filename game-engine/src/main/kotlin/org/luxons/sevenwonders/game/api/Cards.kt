package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.resources.TransactionPlan
import org.luxons.sevenwonders.game.resources.bestSolution

data class TableCard(
    val name: String,
    val color: Color,
    val requirements: Requirements,
    val chainParent: String?,
    val chainChildren: List<String>,
    val image: String,
    val back: CardBack
)

internal fun Card.toTableCard(): TableCard = TableCard(
    name = name,
    color = color,
    requirements = requirements,
    chainParent = chainParent,
    chainChildren = chainChildren,
    image = image,
    back = back
)

/**
 * A card with contextual information relative to the hand it is sitting in. The extra information is especially useful
 * because it frees the client from a painful business logic implementation.
 */
data class HandCard(
    val name: String,
    val color: Color,
    val requirements: Requirements,
    val chainParent: String?,
    val chainChildren: List<String>,
    val image: String,
    val back: CardBack,
    val isChainable: Boolean,
    val isFree: Boolean,
    val isPlayable: Boolean,
    val cheapestTransactions: TransactionPlan
)

internal fun Card.toHandCard(player: Player): HandCard = HandCard(
    name = name,
    color = color,
    requirements = requirements,
    chainParent = chainParent,
    chainChildren = chainChildren,
    image = image,
    back = back,
    isChainable = isChainableOn(player.board),
    isFree = isFreeFor(player.board),
    isPlayable = isPlayableBy(player),
    cheapestTransactions = bestSolution(requirements.resources, player)
)

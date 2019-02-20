package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.CardPlayability
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.cards.Requirements
import org.luxons.sevenwonders.game.moves.Move

data class TableCard(
    val name: String,
    val color: Color,
    val requirements: Requirements,
    val chainParent: String?,
    val chainChildren: List<String>,
    val image: String,
    val back: CardBack,
    val playedDuringLastMove: Boolean
)

internal fun Card.toTableCard(lastMove: Move? = null): TableCard = TableCard(
    name = name,
    color = color,
    requirements = requirements,
    chainParent = chainParent,
    chainChildren = chainChildren,
    image = image,
    back = back,
    playedDuringLastMove = lastMove != null && this.name == lastMove.card.name
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
    val playability: CardPlayability
)

internal fun Card.toHandCard(player: Player): HandCard = HandCard(
    name = name,
    color = color,
    requirements = requirements,
    chainParent = chainParent,
    chainChildren = chainChildren,
    image = image,
    back = back,
    playability = computePlayabilityBy(player)
)

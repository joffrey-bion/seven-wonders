package org.luxons.sevenwonders.game.api.cards

import org.luxons.sevenwonders.game.api.boards.ApiRequirements
import org.luxons.sevenwonders.game.api.resources.ResourceTransactions

data class TableCard(
    val name: String,
    val color: Color,
    val requirements: ApiRequirements,
    val chainParent: String?,
    val chainChildren: List<String>,
    val image: String,
    val back: CardBack,
    val playedDuringLastMove: Boolean
)

/**
 * A card with contextual information relative to the hand it is sitting in. The extra information is especially useful
 * because it frees the client from a painful business logic implementation.
 */
data class HandCard(
    val name: String,
    val color: Color,
    val requirements: ApiRequirements,
    val chainParent: String?,
    val chainChildren: List<String>,
    val image: String,
    val back: CardBack,
    val playability: CardPlayability
)

data class CardBack(val image: String)

enum class PlayabilityLevel {
    CHAINABLE,
    NO_REQUIREMENTS,
    ENOUGH_RESOURCES,
    ENOUGH_GOLD,
    ENOUGH_GOLD_AND_RES,
    REQUIRES_HELP,
    MISSING_REQUIRED_GOLD,
    MISSING_GOLD_FOR_RES,
    UNAVAILABLE_RESOURCES,
    INCOMPATIBLE_WITH_BOARD
}

enum class Color(val isResource: Boolean) {
    BROWN(true),
    GREY(true),
    YELLOW(false),
    BLUE(false),
    GREEN(false),
    RED(false),
    PURPLE(false)
}

data class CardPlayability(
    val isPlayable: Boolean,
    val isChainable: Boolean = false,
    val minPrice: Int = Int.MAX_VALUE,
    val cheapestTransactions: Set<ResourceTransactions> = emptySet(),
    val playabilityLevel: PlayabilityLevel
) {
    val isFree: Boolean = minPrice == 0
}

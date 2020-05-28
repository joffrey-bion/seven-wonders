package org.luxons.sevenwonders.model.cards

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.boards.Requirements
import org.luxons.sevenwonders.model.resources.ResourceTransactions

interface Card {
    val name: String
    val color: Color
    val requirements: Requirements
    val chainParent: String?
    val chainChildren: List<String>
    val image: String
    val back: CardBack
}

@Serializable
data class TableCard(
    override val name: String,
    override val color: Color,
    override val requirements: Requirements,
    override val chainParent: String?,
    override val chainChildren: List<String>,
    override val image: String,
    override val back: CardBack,
    val playedDuringLastMove: Boolean
) : Card

/**
 * A card with contextual information relative to the hand it is sitting in. The extra information is especially useful
 * because it frees the client from a painful business logic implementation.
 */
@Serializable
data class HandCard(
    override val name: String,
    override val color: Color,
    override val requirements: Requirements,
    override val chainParent: String?,
    override val chainChildren: List<String>,
    override val image: String,
    override val back: CardBack,
    val playability: CardPlayability
) : Card

@Serializable
data class PreparedCard(
    val player: PlayerDTO,
    val cardBack: CardBack?
)

@Serializable
data class CardBack(val image: String)

enum class PlayabilityLevel(val message: String) {
    CHAINABLE("free because of a card on the board"),
    NO_REQUIREMENTS("free"),
    SPECIAL_FREE("free because of a special ability"),
    ENOUGH_RESOURCES("free"),
    ENOUGH_GOLD("enough gold"),
    ENOUGH_GOLD_AND_RES("enough gold and resources"),
    REQUIRES_HELP("requires buying resources"),
    MISSING_REQUIRED_GOLD("not enough gold"),
    MISSING_GOLD_FOR_RES("not enough gold to buy resources"),
    UNAVAILABLE_RESOURCES("neighbours don't have the missing resources"),
    INCOMPATIBLE_WITH_BOARD("card already on the board")
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

@Serializable
data class CardPlayability(
    val isPlayable: Boolean,
    val isChainable: Boolean = false,
    val minPrice: Int = Int.MAX_VALUE,
    val cheapestTransactions: Set<ResourceTransactions> = emptySet(),
    val playabilityLevel: PlayabilityLevel
) {
    val isFree: Boolean = minPrice == 0
}

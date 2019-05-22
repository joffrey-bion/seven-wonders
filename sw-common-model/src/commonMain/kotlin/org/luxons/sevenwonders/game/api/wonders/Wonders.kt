package org.luxons.sevenwonders.game.api.wonders

import org.luxons.sevenwonders.game.api.boards.ApiRequirements
import org.luxons.sevenwonders.game.api.cards.CardBack
import org.luxons.sevenwonders.game.api.cards.PlayabilityLevel
import org.luxons.sevenwonders.game.api.resources.ResourceTransactions
import org.luxons.sevenwonders.game.api.resources.ResourceType

data class ApiWonder(
    val name: String,
    val initialResource: ResourceType,
    val stages: List<ApiWonderStage>,
    val image: String,
    val nbBuiltStages: Int,
    val buildability: WonderBuildability
)

data class ApiWonderStage(
    val cardBack: CardBack?,
    val isBuilt: Boolean,
    val requirements: ApiRequirements,
    val builtDuringLastMove: Boolean
)

data class WonderBuildability(
    val isBuildable: Boolean,
    val minPrice: Int = Int.MAX_VALUE,
    val cheapestTransactions: Set<ResourceTransactions> = emptySet(),
    val playabilityLevel: PlayabilityLevel
) {
    val isFree: Boolean = minPrice == 0
}

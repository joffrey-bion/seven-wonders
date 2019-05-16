package org.luxons.sevenwonders.game.wonders

import org.luxons.sevenwonders.game.api.ApiRequirements
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.cards.PlayabilityLevel
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.ResourceType

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

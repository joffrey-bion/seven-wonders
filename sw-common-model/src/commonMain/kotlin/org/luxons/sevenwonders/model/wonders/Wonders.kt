package org.luxons.sevenwonders.model.wonders

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.boards.Requirements
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.PlayabilityLevel
import org.luxons.sevenwonders.model.resources.ResourceTransactions
import org.luxons.sevenwonders.model.resources.ResourceType

@Serializable
data class ApiWonder(
    val name: String,
    val initialResource: ResourceType,
    val stages: List<ApiWonderStage>,
    val image: String,
    val nbBuiltStages: Int,
    val buildability: WonderBuildability
)

@Serializable
data class ApiWonderStage(
    val cardBack: CardBack?,
    val isBuilt: Boolean,
    val requirements: Requirements,
    val builtDuringLastMove: Boolean
)

@Serializable
data class WonderBuildability(
    val isBuildable: Boolean,
    val minPrice: Int = Int.MAX_VALUE,
    val cheapestTransactions: Set<ResourceTransactions> = emptySet(),
    val playabilityLevel: PlayabilityLevel
) {
    val isFree: Boolean = minPrice == 0
}

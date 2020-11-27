package org.luxons.sevenwonders.engine.cards

import org.luxons.sevenwonders.model.cards.PlayabilityLevel
import org.luxons.sevenwonders.model.resources.PricedResourceTransactions
import org.luxons.sevenwonders.model.resources.noTransactions

internal data class RequirementsSatisfaction(
    val satisfied: Boolean,
    val level: PlayabilityLevel,
    val minPrice: Int,
    val cheapestTransactions: Set<PricedResourceTransactions>,
) {
    companion object {

        internal fun noRequirements() =
            RequirementsSatisfaction(true, PlayabilityLevel.NO_REQUIREMENTS, 0, setOf(noTransactions()))

        internal fun enoughResources() =
            RequirementsSatisfaction(true, PlayabilityLevel.ENOUGH_RESOURCES, 0, setOf(noTransactions()))

        internal fun enoughGold(minPrice: Int) =
            RequirementsSatisfaction(true, PlayabilityLevel.ENOUGH_GOLD, minPrice, setOf(noTransactions()))

        internal fun enoughResourcesAndGold(minPrice: Int) =
            RequirementsSatisfaction(true, PlayabilityLevel.ENOUGH_GOLD_AND_RES, minPrice, setOf(noTransactions()))

        internal fun metWithHelp(minPrice: Int, cheapestTransactions: Set<PricedResourceTransactions>) =
            RequirementsSatisfaction(true, PlayabilityLevel.REQUIRES_HELP, minPrice, cheapestTransactions)

        internal fun missingRequiredGold(minPrice: Int) =
            RequirementsSatisfaction(false, PlayabilityLevel.MISSING_REQUIRED_GOLD, minPrice, emptySet())

        internal fun missingGoldForResources(minPrice: Int, cheapestTransactions: Set<PricedResourceTransactions>) =
            RequirementsSatisfaction(false, PlayabilityLevel.MISSING_GOLD_FOR_RES, minPrice, cheapestTransactions)

        internal fun unavailableResources() =
            RequirementsSatisfaction(false, PlayabilityLevel.UNAVAILABLE_RESOURCES, Int.MAX_VALUE, emptySet())
    }
}

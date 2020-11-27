package org.luxons.sevenwonders.engine.cards

import org.luxons.sevenwonders.model.cards.PlayabilityLevel
import org.luxons.sevenwonders.model.resources.ResourceTransactionOptions
import org.luxons.sevenwonders.model.resources.noTransactionOptions
import org.luxons.sevenwonders.model.resources.singleOptionNoTransactionNeeded

internal data class RequirementsSatisfaction(
    val satisfied: Boolean,
    val level: PlayabilityLevel,
    val minPrice: Int,
    val transactionOptions: ResourceTransactionOptions,
) {
    companion object {

        internal fun noRequirements() =
            RequirementsSatisfaction(true, PlayabilityLevel.NO_REQUIREMENTS, 0, singleOptionNoTransactionNeeded())

        internal fun enoughResources() =
            RequirementsSatisfaction(true, PlayabilityLevel.ENOUGH_RESOURCES, 0, singleOptionNoTransactionNeeded())

        internal fun enoughGold(minPrice: Int) =
            RequirementsSatisfaction(true, PlayabilityLevel.ENOUGH_GOLD, minPrice, singleOptionNoTransactionNeeded())

        internal fun enoughResourcesAndGold(minPrice: Int) =
            RequirementsSatisfaction(true, PlayabilityLevel.ENOUGH_GOLD_AND_RES, minPrice, singleOptionNoTransactionNeeded())

        internal fun metWithHelp(minPrice: Int, transactionOptions: ResourceTransactionOptions) =
            RequirementsSatisfaction(true, PlayabilityLevel.REQUIRES_HELP, minPrice, transactionOptions)

        internal fun missingRequiredGold(minPrice: Int) =
            RequirementsSatisfaction(false, PlayabilityLevel.MISSING_REQUIRED_GOLD, minPrice, noTransactionOptions())

        internal fun missingGoldForResources(minPrice: Int, transactionOptions: ResourceTransactionOptions) =
            RequirementsSatisfaction(false, PlayabilityLevel.MISSING_GOLD_FOR_RES, minPrice, transactionOptions)

        internal fun unavailableResources() =
            RequirementsSatisfaction(false, PlayabilityLevel.UNAVAILABLE_RESOURCES, Int.MAX_VALUE, noTransactionOptions())
    }
}

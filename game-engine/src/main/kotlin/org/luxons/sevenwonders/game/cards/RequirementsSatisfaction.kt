package org.luxons.sevenwonders.game.cards

import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.noTransactions

internal sealed class RequirementsSatisfaction(
    val satisfied: Boolean,
    val minPrice: Int,
    val cheapestTransactions: Set<ResourceTransactions>
) {
    sealed class Acceptable(minPrice: Int, cheapestTransactions: Set<ResourceTransactions>) :
        RequirementsSatisfaction(true, minPrice, cheapestTransactions) {

        sealed class WithoutHelp(minPrice: Int) : Acceptable(minPrice, setOf(noTransactions())) {

            sealed class Free : WithoutHelp(0) {

                object NoRequirement : Free()

                object EnoughResources : Free()
            }

            class EnoughGold(minPrice: Int) : WithoutHelp(minPrice)

            class EnoughResourcesAndGold(minPrice: Int) : WithoutHelp(minPrice)
        }

        class WithHelp(minPrice: Int, cheapestTransactions: Set<ResourceTransactions>) :
            Acceptable(minPrice, cheapestTransactions)
    }

    sealed class Insufficient(minPrice: Int, cheapestTransactions: Set<ResourceTransactions>) :
        RequirementsSatisfaction(false, minPrice, cheapestTransactions) {

        class MissingRequiredGold(minPrice: Int) : Insufficient(minPrice, emptySet())

        class MissingGoldForResources(minPrice: Int, cheapestTransactions: Set<ResourceTransactions>) :
            Insufficient(minPrice, cheapestTransactions)

        object UnavailableResources : Insufficient(Int.MAX_VALUE, emptySet())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequirementsSatisfaction

        if (satisfied != other.satisfied) return false
        if (minPrice != other.minPrice) return false
        if (cheapestTransactions != other.cheapestTransactions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = satisfied.hashCode()
        result = 31 * result + minPrice
        result = 31 * result + cheapestTransactions.hashCode()
        return result
    }

    companion object {

        internal fun noRequirements() = RequirementsSatisfaction.Acceptable.WithoutHelp.Free.NoRequirement

        internal fun enoughResources() = RequirementsSatisfaction.Acceptable.WithoutHelp.Free.EnoughResources

        internal fun enoughGold(minPrice: Int) = RequirementsSatisfaction.Acceptable.WithoutHelp.EnoughGold(minPrice)

        internal fun enoughResourcesAndGold(minPrice: Int) =
            RequirementsSatisfaction.Acceptable.WithoutHelp.EnoughResourcesAndGold(minPrice)

        internal fun metWithHelp(minPrice: Int, cheapestTransactions: Set<ResourceTransactions>) =
            RequirementsSatisfaction.Acceptable.WithHelp(minPrice, cheapestTransactions)

        internal fun missingRequiredGold(minPrice: Int) = RequirementsSatisfaction.Insufficient.MissingRequiredGold(minPrice)

        internal fun missingGoldForResources(minPrice: Int, cheapestTransactions: Set<ResourceTransactions>) =
            RequirementsSatisfaction.Insufficient.MissingGoldForResources(minPrice, cheapestTransactions)

        internal fun resourcesUnavailable() = RequirementsSatisfaction.Insufficient.UnavailableResources
    }
}

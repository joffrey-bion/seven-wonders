package org.luxons.sevenwonders.game.resources

class TradingRules internal constructor(private val defaultCost: Int) {

    private val costs: MutableMap<ResourceType, MutableMap<Provider, Int>> = mutableMapOf()

    fun getCosts(): Map<ResourceType, Map<Provider, Int>> {
        return costs
    }

    internal fun getCost(type: ResourceType, provider: Provider): Int =
        costs.computeIfAbsent(type) { mutableMapOf() }.getOrDefault(provider, defaultCost)

    internal fun setCost(type: ResourceType, provider: Provider, cost: Int) {
        costs.computeIfAbsent(type) { mutableMapOf() }[provider] = cost
    }

    internal fun computeCost(transactions: ResourceTransactions): Int {
        return transactions.asList().map { this.computeCost(it) }.sum()
    }

    internal fun computeCost(transaction: ResourceTransaction): Int {
        val resources = transaction.resources
        val provider = transaction.provider
        return computeCost(resources, provider)
    }

    private fun computeCost(resources: Resources, provider: Provider): Int {
        var total = 0
        for (type in ResourceType.values()) {
            val count = resources.getQuantity(type)
            total += getCost(type, provider) * count
        }
        return total
    }
}

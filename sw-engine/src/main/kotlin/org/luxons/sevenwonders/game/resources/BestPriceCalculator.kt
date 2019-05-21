package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.Player
import org.luxons.sevenwonders.game.api.resources.Provider
import org.luxons.sevenwonders.game.api.resources.ResourceTransactions
import org.luxons.sevenwonders.game.api.resources.ResourceType
import java.util.EnumSet

internal fun bestSolution(resources: Resources, player: Player): TransactionPlan =
    BestPriceCalculator(resources, player).computeBestSolution()

data class TransactionPlan(val price: Int, val possibleTransactions: Set<ResourceTransactions>)

private class ResourcePool(
    val provider: Provider?,
    private val rules: TradingRules,
    choices: Set<Set<ResourceType>>
) {
    val choices: Set<MutableSet<ResourceType>> = choices.map { it.toMutableSet() }.toSet()

    fun getCost(type: ResourceType): Int = if (provider == null) 0 else rules.getCost(type, provider)
}

private class BestPriceCalculator(resourcesToPay: Resources, player: Player) {

    private val pools: List<ResourcePool>
    private val resourcesLeftToPay: MutableResources
    private val boughtResources: MutableMap<Provider, MutableResources> = HashMap()
    private var pricePaid: Int = 0

    private var bestSolutions: MutableSet<ResourceTransactions> = mutableSetOf()
    private var bestPrice: Int = Integer.MAX_VALUE

    init {
        val board = player.board
        this.resourcesLeftToPay = resourcesToPay.minus(board.production.getFixedResources()).toMutableResources()
        this.pools = createResourcePools(player)
    }

    private fun createResourcePools(player: Player): List<ResourcePool> {
        // we only take alternative resources here, because fixed resources were already removed for optimization
        val ownBoardChoices = player.board.production.getAlternativeResources()
        val ownPool = ResourcePool(null, player.board.tradingRules, ownBoardChoices)
        val providerPools = Provider.values().map { it.toResourcePoolFor(player) }

        return providerPools + ownPool
    }

    private fun Provider.toResourcePoolFor(player: Player): ResourcePool {
        val providerBoard = player.getBoard(boardPosition)
        val choices = providerBoard.publicProduction.asChoices()
        return ResourcePool(this, player.board.tradingRules, choices)
    }

    fun computeBestSolution(): TransactionPlan {
        computePossibilities()
        return TransactionPlan(bestPrice, bestSolutions)
    }

    private fun computePossibilities() {
        if (resourcesLeftToPay.isEmpty()) {
            updateBestSolutionIfNeeded()
            return
        }
        for (type in ResourceType.values()) {
            if (resourcesLeftToPay[type] > 0) {
                for (pool in pools) {
                    if (pool.provider == null) {
                        computeSelfPossibilities(type, pool)
                    } else {
                        computeNeighbourPossibilities(pool, type, pool.provider)
                    }
                }
            }
        }
    }

    private fun computeSelfPossibilities(type: ResourceType, pool: ResourcePool) {
        resourcesLeftToPay.remove(type, 1)
        computePossibilitiesWhenUsing(type, pool)
        resourcesLeftToPay.add(type, 1)
    }

    private fun computeNeighbourPossibilities(pool: ResourcePool, type: ResourceType, provider: Provider) {
        val cost = pool.getCost(type)
        resourcesLeftToPay.remove(type, 1)
        buyOne(provider, type, cost)
        computePossibilitiesWhenUsing(type, pool)
        unbuyOne(provider, type, cost)
        resourcesLeftToPay.add(type, 1)
    }

    fun buyOne(provider: Provider, type: ResourceType, cost: Int) {
        boughtResources.getOrPut(provider) { MutableResources() }.add(type, 1)
        pricePaid += cost
    }

    fun unbuyOne(provider: Provider, type: ResourceType, cost: Int) {
        pricePaid -= cost
        boughtResources.get(provider)!!.remove(type, 1)
    }

    private fun computePossibilitiesWhenUsing(type: ResourceType, pool: ResourcePool) {
        for (choice in pool.choices) {
            if (choice.contains(type)) {
                val temp = EnumSet.copyOf(choice)
                choice.clear()
                computePossibilities()
                choice.addAll(temp)
            }
        }
    }

    private fun updateBestSolutionIfNeeded() {
        if (pricePaid > bestPrice) return

        if (pricePaid < bestPrice) {
            bestPrice = pricePaid
            bestSolutions.clear()
        }
        // avoid mutating the resources from the transactions
        val transactionSet = boughtResources.mapValues { (_, res) -> res.copy() }.toTransactions()
        bestSolutions.add(transactionSet)
    }
}

package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.Player
import java.util.ArrayList
import java.util.EnumSet

internal fun bestSolution(resources: Resources, player: Player): TransactionPlan =
    BestPriceCalculator(resources, player).computeBestSolution()

internal data class TransactionPlan(val price: Int, val possibleTransactions: Set<ResourceTransactions>)

private class ResourcePool(
    val provider: Provider?,
    private val rules: TradingRules,
    val choices: Set<MutableSet<ResourceType>>
) {
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
        val providers = Provider.values()

        val board = player.board
        val rules = board.tradingRules

        val pools = ArrayList<ResourcePool>(providers.size + 1)
        // we only take alternative resources here, because fixed resources were already removed for optimization
        val ownBoardChoices = board.production.getAlternativeResources()
        pools.add(ResourcePool(null, rules, ownBoardChoices.map { it.toMutableSet() }.toSet()))

        for (provider in providers) {
            val providerBoard = player.getBoard(provider.boardPosition)
            val choices = providerBoard.publicProduction.asChoices().map { it.toMutableSet() }.toSet()
            val pool = ResourcePool(provider, rules, choices)
            pools.add(pool)
        }
        return pools
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
        bestSolutions.add(boughtResources.mapValues { MutableResources(HashMap(it.value.quantities)) }.toTransactions())
    }
}

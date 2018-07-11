package org.luxons.sevenwonders.game.resources

import org.luxons.sevenwonders.game.Player
import java.util.ArrayList
import java.util.EnumSet

fun bestPrice(resources: Resources, player: Player): Int? {
    return bestSolution(resources, player)?.price
}

fun bestTransaction(resources: Resources, player: Player): ResourceTransactions? {
    return bestSolution(resources, player)?.transactions
}

fun bestSolution(resources: Resources, player: Player): TransactionPlan? {
    val calculator = BestPriceCalculator(resources, player)
    return calculator.computeBestSolution()
}

data class TransactionPlan(val price: Int, val transactions: ResourceTransactions)

private class ResourcePool(
    val provider: Provider?,
    private val rules: TradingRules,
    val choices: Set<MutableSet<ResourceType>>
) {
    fun getCost(type: ResourceType): Int = if (provider == null) 0 else rules.getCost(type, provider)
}

private class BestPriceCalculator(resourcesToPay: Resources, player: Player) {

    private val pools: List<ResourcePool>
    private val resourcesLeftToPay: Resources
    private val boughtResources: ResourceTransactions = ResourceTransactions()
    private var pricePaid: Int = 0

    var bestSolution: ResourceTransactions? = null
    var bestPrice: Int = Integer.MAX_VALUE

    init {
        val board = player.board
        this.resourcesLeftToPay = resourcesToPay.minus(board.production.fixedResources)
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
            val pool = ResourcePool(provider, rules, providerBoard.publicProduction.asChoices().map { it.toMutableSet() }.toSet())
            pools.add(pool)
        }
        return pools
    }

    fun computeBestSolution(): TransactionPlan? {
        computePossibilities()
        return if (bestSolution == null) null else TransactionPlan(bestPrice, bestSolution!!)
    }

    private fun computePossibilities() {
        if (resourcesLeftToPay.isEmpty) {
            updateBestSolutionIfNeeded()
            return
        }
        for (type in ResourceType.values()) {
            if (resourcesLeftToPay.getQuantity(type) > 0) {
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
        boughtResources.add(provider, Resources(type))
        pricePaid += cost
        computePossibilitiesWhenUsing(type, pool)
        pricePaid -= cost
        boughtResources.remove(provider, Resources(type))
        resourcesLeftToPay.add(type, 1)
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
        if (pricePaid < bestPrice) {
            bestPrice = pricePaid
            bestSolution = ResourceTransactions(boughtResources.asList())
        }
    }
}

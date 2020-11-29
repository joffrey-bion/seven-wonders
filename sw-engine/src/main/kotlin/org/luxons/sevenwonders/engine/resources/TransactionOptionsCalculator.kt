package org.luxons.sevenwonders.engine.resources

import org.luxons.sevenwonders.engine.Player
import org.luxons.sevenwonders.model.resources.*
import java.util.*

internal fun transactionOptions(resources: Resources, player: Player): ResourceTransactionOptions =
    TransactionOptionsCalculator(resources, player).computeOptions()

private class ResourcePool(
    val provider: Provider?,
    private val rules: TradingRules,
    choices: List<Set<ResourceType>>,
) {
    val choices: List<MutableSet<ResourceType>> = choices.map { it.toMutableSet() }

    fun getCost(type: ResourceType): Int = if (provider == null) 0 else rules.getCost(type, provider)
}

private class TransactionOptionsCalculator(resourcesToPay: Resources, player: Player) {

    private val pools: List<ResourcePool>
    private val resourcesLeftToPay: MutableResources
    private val boughtResources: MutableMap<Provider, MutableResources> = EnumMap(Provider::class.java)
    private val pricePaidPerProvider: MutableMap<Provider, Int> = EnumMap(Provider::class.java)

    private var optionsSoFar: MutableSet<PricedResourceTransactions> = mutableSetOf()

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

    fun computeOptions(): ResourceTransactionOptions {
        computePossibilities()
        return optionsSoFar.distinctBy { it.costByProvider }.sortedBy { it.totalPrice }
    }

    private val PricedResourceTransactions.costByProvider: Map<Provider, Int>
        get() = associate { it.provider to it.totalPrice }

    private fun computePossibilities() {
        if (resourcesLeftToPay.isEmpty()) {
            addCurrentOption()
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
        pricePaidPerProvider.merge(provider, cost) { old, new -> old + new }
    }

    fun unbuyOne(provider: Provider, type: ResourceType, cost: Int) {
        pricePaidPerProvider.merge(provider, -cost) { old, new -> old + new }
        boughtResources[provider]!!.remove(type, 1)
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

    private fun addCurrentOption() {
        if (optionsSoFar.any { it < pricePaidPerProvider }) {
            return
        }
        // avoid mutating the resources from the transactions
        val transactionsOption = boughtResources.mapValues { (_, res) -> res.copy() }.toTransactions(pricePaidPerProvider)
        optionsSoFar.add(transactionsOption)
        optionsSoFar.removeIf { it > pricePaidPerProvider }
    }

    private operator fun PricedResourceTransactions.compareTo(prices: Map<Provider, Int>): Int = when {
        left == prices.left -> right.compareTo(prices.right)
        right == prices.right -> left.compareTo(prices.left)
        left < prices.left && right < prices.right -> -1
        left > prices.left && right > prices.right -> 1
        else -> 0
    }

    private val Map<Provider, Int>.left: Int get() = this[Provider.LEFT_PLAYER] ?: 0
    private val Map<Provider, Int>.right: Int get() = this[Provider.RIGHT_PLAYER] ?: 0

    private val PricedResourceTransactions.left: Int
        get() = firstOrNull { it.provider == Provider.LEFT_PLAYER }?.totalPrice ?: 0
    private val PricedResourceTransactions.right: Int
        get() = firstOrNull { it.provider == Provider.RIGHT_PLAYER }?.totalPrice ?: 0
}

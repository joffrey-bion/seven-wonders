package org.luxons.sevenwonders.game.resources

import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.test.createTransaction
import org.luxons.sevenwonders.game.test.createTransactions
import java.util.ArrayList

@RunWith(Theories::class)
class TradingRulesTest {

    @Theory
    fun setCost_overridesCost(
        defaultCost: Int,
        overriddenCost: Int,
        overriddenProvider: Provider,
        provider: Provider,
        type: ResourceType
    ) {
        assumeTrue(defaultCost != overriddenCost)
        assumeTrue(overriddenProvider != provider)

        val rules = TradingRules(defaultCost)
        rules.setCost(type, overriddenProvider, overriddenCost)

        assertEquals(overriddenCost.toLong(), rules.getCost(type, overriddenProvider).toLong())
        assertEquals(defaultCost.toLong(), rules.getCost(type, provider).toLong())
    }

    @Theory
    fun computeCost_zeroForNoResources(defaultCost: Int) {
        val rules = TradingRules(defaultCost)
        assertEquals(0, rules.computeCost(noTransactions()).toLong())
    }

    @Theory
    fun computeCost_defaultCostWhenNoOverride(defaultCost: Int, provider: Provider, type: ResourceType) {
        val rules = TradingRules(defaultCost)
        val transactions = createTransactions(provider, type)
        assertEquals(defaultCost.toLong(), rules.computeCost(transactions).toLong())
    }

    @Theory
    fun computeCost_twiceDefaultFor2Resources(defaultCost: Int, provider: Provider, type: ResourceType) {
        val rules = TradingRules(defaultCost)
        val transactions = createTransactions(provider, type, type)
        assertEquals((2 * defaultCost).toLong(), rules.computeCost(transactions).toLong())
    }

    @Theory
    fun computeCost_overriddenCost(defaultCost: Int, overriddenCost: Int, provider: Provider, type: ResourceType) {
        val rules = TradingRules(defaultCost)
        rules.setCost(type, provider, overriddenCost)
        val transactions = createTransactions(provider, type)
        assertEquals(overriddenCost.toLong(), rules.computeCost(transactions).toLong())
    }

    @Theory
    fun computeCost_defaultCostWhenOverrideOnOtherProviderOrType(
        defaultCost: Int,
        overriddenCost: Int,
        overriddenProvider: Provider,
        overriddenType: ResourceType,
        provider: Provider,
        type: ResourceType
    ) {
        assumeTrue(overriddenProvider != provider || overriddenType != type)
        val rules = TradingRules(defaultCost)
        rules.setCost(overriddenType, overriddenProvider, overriddenCost)
        val transactions = createTransactions(provider, type)
        assertEquals(defaultCost.toLong(), rules.computeCost(transactions).toLong())
    }

    @Theory
    fun computeCost_oneDefaultAndOneOverriddenType(
        defaultCost: Int,
        overriddenCost: Int,
        overriddenType: ResourceType,
        provider: Provider,
        type: ResourceType
    ) {
        assumeTrue(overriddenType != type)
        val rules = TradingRules(defaultCost)
        rules.setCost(overriddenType, provider, overriddenCost)
        val transactions = createTransactions(provider, overriddenType, type)
        assertEquals((defaultCost + overriddenCost).toLong(), rules.computeCost(transactions).toLong())
    }

    @Theory
    fun computeCost_oneDefaultAndOneOverriddenProvider(
        defaultCost: Int,
        overriddenCost: Int,
        overriddenProvider: Provider,
        provider: Provider,
        type: ResourceType
    ) {
        assumeTrue(overriddenProvider != provider)
        val rules = TradingRules(defaultCost)
        rules.setCost(type, overriddenProvider, overriddenCost)

        val boughtResources = ArrayList<ResourceTransaction>(2)
        boughtResources.add(createTransaction(provider, type))
        boughtResources.add(createTransaction(overriddenProvider, type))

        assertEquals(defaultCost + overriddenCost, rules.computeCost(boughtResources))
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun costs(): IntArray {
            return intArrayOf(0, 1, 2)
        }

        @JvmStatic
        @DataPoints
        fun providers(): Array<Provider> {
            return Provider.values()
        }

        @JvmStatic
        @DataPoints
        fun resourceTypes(): Array<ResourceType> {
            return ResourceType.values()
        }
    }
}

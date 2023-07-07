package org.luxons.sevenwonders.engine.resources

import org.junit.Assume.assumeTrue
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.test.createTransaction
import org.luxons.sevenwonders.engine.test.createTransactions
import org.luxons.sevenwonders.model.resources.Provider
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.resources.noTransactions
import kotlin.enums.EnumEntries
import kotlin.test.assertEquals

@RunWith(Theories::class)
class TradingRulesTest {

    @Theory
    fun setCost_overridesCost(
        defaultCost: Int,
        overriddenCost: Int,
        overriddenProvider: Provider,
        provider: Provider,
        type: ResourceType,
    ) {
        assumeTrue(defaultCost != overriddenCost)
        assumeTrue(overriddenProvider != provider)

        val rules = TradingRules(defaultCost)
        rules.setCost(type, overriddenProvider, overriddenCost)

        assertEquals(overriddenCost, rules.getCost(type, overriddenProvider))
        assertEquals(defaultCost, rules.getCost(type, provider))
    }

    @Theory
    fun computeCost_zeroForNoResources(defaultCost: Int) {
        val rules = TradingRules(defaultCost)
        assertEquals(0, rules.computeCost(noTransactions()))
    }

    @Theory
    fun computeCost_defaultCostWhenNoOverride(defaultCost: Int, provider: Provider, type: ResourceType) {
        val rules = TradingRules(defaultCost)
        val transactions = createTransactions(provider, type)
        assertEquals(defaultCost, rules.computeCost(transactions))
    }

    @Theory
    fun computeCost_twiceDefaultFor2Resources(defaultCost: Int, provider: Provider, type: ResourceType) {
        val rules = TradingRules(defaultCost)
        val transactions = createTransactions(provider, type, type)
        assertEquals(2 * defaultCost, rules.computeCost(transactions))
    }

    @Theory
    fun computeCost_overriddenCost(defaultCost: Int, overriddenCost: Int, provider: Provider, type: ResourceType) {
        val rules = TradingRules(defaultCost)
        rules.setCost(type, provider, overriddenCost)
        val transactions = createTransactions(provider, type)
        assertEquals(overriddenCost, rules.computeCost(transactions))
    }

    @Theory
    fun computeCost_defaultCostWhenOverrideOnOtherProviderOrType(
        defaultCost: Int,
        overriddenCost: Int,
        overriddenProvider: Provider,
        overriddenType: ResourceType,
        provider: Provider,
        type: ResourceType,
    ) {
        assumeTrue(overriddenProvider != provider || overriddenType != type)
        val rules = TradingRules(defaultCost)
        rules.setCost(overriddenType, overriddenProvider, overriddenCost)
        val transactions = createTransactions(provider, type)
        assertEquals(defaultCost, rules.computeCost(transactions))
    }

    @Theory
    fun computeCost_oneDefaultAndOneOverriddenType(
        defaultCost: Int,
        overriddenCost: Int,
        overriddenType: ResourceType,
        provider: Provider,
        type: ResourceType,
    ) {
        assumeTrue(overriddenType != type)
        val rules = TradingRules(defaultCost)
        rules.setCost(overriddenType, provider, overriddenCost)
        val transactions = createTransactions(provider, overriddenType, type)
        assertEquals(defaultCost + overriddenCost, rules.computeCost(transactions))
    }

    @Theory
    fun computeCost_oneDefaultAndOneOverriddenProvider(
        defaultCost: Int,
        overriddenCost: Int,
        overriddenProvider: Provider,
        provider: Provider,
        type: ResourceType,
    ) {
        assumeTrue(overriddenProvider != provider)
        val rules = TradingRules(defaultCost)
        rules.setCost(type, overriddenProvider, overriddenCost)

        val boughtResources = createTransactions(
            createTransaction(provider, type),
            createTransaction(overriddenProvider, type),
        )

        assertEquals(defaultCost + overriddenCost, rules.computeCost(boughtResources))
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun costs(): IntArray = intArrayOf(0, 1, 2)

        @JvmStatic
        @DataPoints
        fun providers(): EnumEntries<Provider> = Provider.entries

        @JvmStatic
        @DataPoints
        fun resourceTypes(): EnumEntries<ResourceType> = ResourceType.entries
    }
}

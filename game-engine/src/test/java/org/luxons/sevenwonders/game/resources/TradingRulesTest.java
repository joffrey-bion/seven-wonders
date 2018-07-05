package org.luxons.sevenwonders.game.resources;

import java.util.ArrayList;
import java.util.List;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class TradingRulesTest {

    @DataPoints
    public static int[] costs() {
        return new int[]{0, 1, 2};
    }

    @DataPoints
    public static Provider[] providers() {
        return Provider.values();
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @Theory
    public void setCost_overridesCost(int defaultCost, int overriddenCost, Provider overriddenProvider,
                                      Provider provider, ResourceType type) {
        assumeTrue(defaultCost != overriddenCost);
        assumeTrue(overriddenProvider != provider);

        TradingRules rules = new TradingRules(defaultCost);
        rules.setCost(type, overriddenProvider, overriddenCost);

        assertEquals(overriddenCost, rules.getCost(type, overriddenProvider));
        assertEquals(defaultCost, rules.getCost(type, provider));
    }

    @Theory
    public void computeCost_zeroForNoResources(int defaultCost) {
        TradingRules rules = new TradingRules(defaultCost);
        assertEquals(0, rules.computeCost(new ResourceTransactions()));
    }

    @Theory
    public void computeCost_defaultCostWhenNoOverride(int defaultCost, Provider provider, ResourceType type) {
        TradingRules rules = new TradingRules(defaultCost);
        ResourceTransactions transactions = TestUtilsKt.createTransactions(provider, type);
        assertEquals(defaultCost, rules.computeCost(transactions));
    }

    @Theory
    public void computeCost_twiceDefaultFor2Resources(int defaultCost, Provider provider, ResourceType type) {
        TradingRules rules = new TradingRules(defaultCost);
        ResourceTransactions transactions = TestUtilsKt.createTransactions(provider, type, type);
        assertEquals(2 * defaultCost, rules.computeCost(transactions));
    }

    @Theory
    public void computeCost_overriddenCost(int defaultCost, int overriddenCost, Provider provider, ResourceType type) {
        TradingRules rules = new TradingRules(defaultCost);
        rules.setCost(type, provider, overriddenCost);
        ResourceTransactions transactions = TestUtilsKt.createTransactions(provider, type);
        assertEquals(overriddenCost, rules.computeCost(transactions));
    }

    @Theory
    public void computeCost_defaultCostWhenOverrideOnOtherProviderOrType(int defaultCost, int overriddenCost,
                                                                         Provider overriddenProvider,
                                                                         ResourceType overriddenType, Provider provider,
                                                                         ResourceType type) {
        assumeTrue(overriddenProvider != provider || overriddenType != type);
        TradingRules rules = new TradingRules(defaultCost);
        rules.setCost(overriddenType, overriddenProvider, overriddenCost);
        ResourceTransactions transactions = TestUtilsKt.createTransactions(provider, type);
        assertEquals(defaultCost, rules.computeCost(transactions));
    }

    @Theory
    public void computeCost_oneDefaultAndOneOverriddenType(int defaultCost, int overriddenCost,
                                                           ResourceType overriddenType, Provider provider,
                                                           ResourceType type) {
        assumeTrue(overriddenType != type);
        TradingRules rules = new TradingRules(defaultCost);
        rules.setCost(overriddenType, provider, overriddenCost);
        ResourceTransactions transactions = TestUtilsKt.createTransactions(provider, overriddenType, type);
        assertEquals(defaultCost + overriddenCost, rules.computeCost(transactions));
    }

    @Theory
    public void computeCost_oneDefaultAndOneOverriddenProvider(int defaultCost, int overriddenCost,
                                                               Provider overriddenProvider, Provider provider,
                                                               ResourceType type) {
        assumeTrue(overriddenProvider != provider);
        TradingRules rules = new TradingRules(defaultCost);
        rules.setCost(type, overriddenProvider, overriddenCost);

        List<ResourceTransaction> boughtResources = new ArrayList<>(2);
        boughtResources.add(TestUtilsKt.createTransaction(provider, type));
        boughtResources.add(TestUtilsKt.createTransaction(overriddenProvider, type));

        assertEquals(defaultCost + overriddenCost, rules.computeCost(new ResourceTransactions(boughtResources)));
    }
}

package org.luxons.sevenwonders.game.resources;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;

public class BestPriceCalculatorTest {

    @Test
    public void bestPrice_0forEmptyResources() {
        Table table = TestUtils.createTable(3);
        Resources resources = new Resources();
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(Collections.emptyList(), BestPriceCalculator.bestSolution(resources, table, 0));
    }

    @Test
    public void bestPrice_fixedResources_defaultCost() {
        Board left = TestUtils.createBoard(ResourceType.STONE);
        Board main = TestUtils.createBoard(ResourceType.STONE);
        Board right = TestUtils.createBoard(ResourceType.WOOD);
        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = new Resources();
        resources.add(ResourceType.STONE, 2);
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(4, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 2));

        BoughtResources stoneLeft = new BoughtResources(Provider.LEFT_PLAYER, Resources.of(ResourceType.STONE));
        BoughtResources stoneRight = new BoughtResources(Provider.RIGHT_PLAYER, Resources.of(ResourceType.STONE));
        assertEquals(Collections.singletonList(stoneLeft), BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(Arrays.asList(stoneLeft, stoneRight), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(Collections.singletonList(stoneRight), BestPriceCalculator.bestSolution(resources, table, 2));
    }

    @Test
    public void bestPrice_fixedResources_overridenCost() {
        Board main = TestUtils.createBoard(ResourceType.STONE);
        main.getTradingRules().setCost(ResourceType.WOOD, Provider.RIGHT_PLAYER, 1);

        Board left = TestUtils.createBoard(ResourceType.WOOD);
        Board right = TestUtils.createBoard(ResourceType.WOOD);
        Board opposite = TestUtils.createBoard(ResourceType.GLASS);
        Table table = new Table(Arrays.asList(main, right, opposite, left));

        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 1);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 2));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 3));

        BoughtResources woodLeft = new BoughtResources(Provider.LEFT_PLAYER, Resources.of(ResourceType.WOOD));
        BoughtResources woodRight = new BoughtResources(Provider.RIGHT_PLAYER, Resources.of(ResourceType.WOOD));
        assertEquals(Collections.singletonList(woodRight), BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(Collections.emptyList(), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(Collections.singletonList(woodLeft), BestPriceCalculator.bestSolution(resources, table, 2));
        assertEquals(Collections.emptyList(), BestPriceCalculator.bestSolution(resources, table, 3));
    }

    @Test
    public void bestPrice_mixedResources_overridenCost() {
        Board left = TestUtils.createBoard(ResourceType.WOOD);

        Board main = TestUtils.createBoard(ResourceType.STONE);
        main.getTradingRules().setCost(ResourceType.WOOD, Provider.RIGHT_PLAYER, 1);

        Board right = TestUtils.createBoard(ResourceType.ORE);
        right.getProduction().addChoice(ResourceType.WOOD, ResourceType.CLAY);
        right.getPublicProduction().addChoice(ResourceType.WOOD, ResourceType.CLAY);

        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 1);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 2));

        BoughtResources woodRight = new BoughtResources(Provider.RIGHT_PLAYER, Resources.of(ResourceType.WOOD));
        assertEquals(Collections.singletonList(woodRight), BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(Collections.emptyList(), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(Collections.emptyList(), BestPriceCalculator.bestSolution(resources, table, 2));
    }

    @Test
    public void bestPrice_chooseCheapest() {
        Board left = TestUtils.createBoard(ResourceType.WOOD);

        Board main = TestUtils.createBoard(ResourceType.WOOD);
        main.getProduction().addChoice(ResourceType.CLAY, ResourceType.ORE);
        main.getTradingRules().setCost(ResourceType.CLAY, Provider.RIGHT_PLAYER, 1);

        Board right = TestUtils.createBoard(ResourceType.WOOD);
        right.getProduction().addFixedResource(ResourceType.ORE, 1);
        right.getProduction().addFixedResource(ResourceType.CLAY, 1);
        right.getPublicProduction().addFixedResource(ResourceType.ORE, 1);
        right.getPublicProduction().addFixedResource(ResourceType.CLAY, 1);

        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = new Resources();
        resources.add(ResourceType.ORE, 1);
        resources.add(ResourceType.CLAY, 1);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(4, BestPriceCalculator.bestPrice(resources, table, 2));

        BoughtResources oreLeft = new BoughtResources(Provider.LEFT_PLAYER, Resources.of(ResourceType.ORE));
        BoughtResources clayLeft = new BoughtResources(Provider.LEFT_PLAYER, Resources.of(ResourceType.CLAY));
        BoughtResources clayRight = new BoughtResources(Provider.RIGHT_PLAYER, Resources.of(ResourceType.CLAY));
        assertEquals(Collections.singletonList(clayRight), BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(Collections.emptyList(), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(Arrays.asList(oreLeft, clayLeft), BestPriceCalculator.bestSolution(resources, table, 2));
    }
}

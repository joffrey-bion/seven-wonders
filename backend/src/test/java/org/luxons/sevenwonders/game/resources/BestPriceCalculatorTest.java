package org.luxons.sevenwonders.game.resources;

import java.util.Arrays;

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
    }

    @Test
    public void bestPrice_fixedResources_defaultCost() {
        Board left = TestUtils.createBoard(ResourceType.STONE);
        Board main = TestUtils.createBoard(ResourceType.STONE);
        Board right = TestUtils.createBoard(ResourceType.WOOD);
        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = new Resources();
        resources.add(ResourceType.STONE, 1);
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 2));
    }

    @Test
    public void bestPrice_fixedResources_overridenCost() {
        Board left = TestUtils.createBoard(ResourceType.WOOD);
        Board main = TestUtils.createBoard(ResourceType.STONE);
        Board right = TestUtils.createBoard(ResourceType.WOOD);
        Board opposite = TestUtils.createBoard(ResourceType.GLASS);
        Table table = new Table(Arrays.asList(main, right, left, opposite));

        main.getTradingRules().setCost(ResourceType.WOOD, Provider.RIGHT_PLAYER, 1);

        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 1);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 2));
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 3));
    }

    @Test
    public void bestPrice_mixedResources_overridenCost() {
        Board left = TestUtils.createBoard(ResourceType.WOOD);
        Board main = TestUtils.createBoard(ResourceType.STONE);
        Board right = TestUtils.createBoard(ResourceType.ORE);
        right.getProduction().addChoice(ResourceType.WOOD, ResourceType.CLAY);
        Table table = new Table(Arrays.asList(main, right, left));

        main.getTradingRules().setCost(ResourceType.WOOD, Provider.RIGHT_PLAYER, 1);

        Resources resources = new Resources();
        resources.add(ResourceType.WOOD, 1);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 2));
    }
}

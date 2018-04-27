package org.luxons.sevenwonders.game.resources;

import java.util.Arrays;

import org.junit.Test;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.luxons.sevenwonders.game.resources.Provider.LEFT_PLAYER;
import static org.luxons.sevenwonders.game.resources.Provider.RIGHT_PLAYER;
import static org.luxons.sevenwonders.game.resources.ResourceType.CLAY;
import static org.luxons.sevenwonders.game.resources.ResourceType.GLASS;
import static org.luxons.sevenwonders.game.resources.ResourceType.ORE;
import static org.luxons.sevenwonders.game.resources.ResourceType.STONE;
import static org.luxons.sevenwonders.game.resources.ResourceType.WOOD;

public class BestPriceCalculatorTest {

    @Test
    public void bestPrice_0forEmptyResources() {
        Table table = TestUtils.createTable(3);
        Resources resources = new Resources();
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 0));
    }

    @Test
    public void bestPrice_fixedResources_defaultCost() {
        Board left = TestUtils.createBoard(STONE);
        Board main = TestUtils.createBoard(STONE);
        Board right = TestUtils.createBoard(WOOD);
        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = TestUtils.createResources(STONE, STONE);
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(4, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 2));

        ResourceTransaction stoneLeftSingle = TestUtils.createTransaction(LEFT_PLAYER, STONE);
        ResourceTransaction stoneRightSingle = TestUtils.createTransaction(RIGHT_PLAYER, STONE);

        ResourceTransactions stoneLeft = TestUtils.createTransactions(stoneLeftSingle);
        ResourceTransactions stoneRight = TestUtils.createTransactions(stoneRightSingle);
        ResourceTransactions stoneLeftAndRight = TestUtils.createTransactions(stoneLeftSingle, stoneRightSingle);

        assertEquals(stoneLeft, BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(stoneLeftAndRight, BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(stoneRight, BestPriceCalculator.bestSolution(resources, table, 2));
    }

    @Test
    public void bestPrice_fixedResources_overridenCost() {
        Board main = TestUtils.createBoard(STONE);
        main.getTradingRules().setCost(WOOD, RIGHT_PLAYER, 1);

        Board left = TestUtils.createBoard(WOOD);
        Board right = TestUtils.createBoard(WOOD);
        Board opposite = TestUtils.createBoard(GLASS);
        Table table = new Table(Arrays.asList(main, right, opposite, left));

        Resources resources = TestUtils.createResources(WOOD);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 2));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 3));

        ResourceTransactions woodLeft = TestUtils.createTransactions(LEFT_PLAYER, WOOD);
        ResourceTransactions woodRight = TestUtils.createTransactions(RIGHT_PLAYER, WOOD);
        assertEquals(woodRight, BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(woodLeft, BestPriceCalculator.bestSolution(resources, table, 2));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 3));
    }

    @Test
    public void bestPrice_mixedResources_overridenCost() {
        Board left = TestUtils.createBoard(WOOD);

        Board main = TestUtils.createBoard(STONE);
        main.getTradingRules().setCost(WOOD, RIGHT_PLAYER, 1);

        Board right = TestUtils.createBoard(ORE);
        right.getProduction().addChoice(WOOD, CLAY);
        right.getPublicProduction().addChoice(WOOD, CLAY);

        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = TestUtils.createResources(WOOD);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 2));

        ResourceTransactions woodRight = TestUtils.createTransactions(RIGHT_PLAYER, WOOD);

        assertEquals(woodRight, BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 2));
    }

    @Test
    public void bestPrice_chooseCheapest() {
        Board left = TestUtils.createBoard(WOOD);

        Board main = TestUtils.createBoard(WOOD);
        main.getProduction().addChoice(CLAY, ORE);
        main.getTradingRules().setCost(CLAY, RIGHT_PLAYER, 1);

        Board right = TestUtils.createBoard(WOOD);
        right.getProduction().addFixedResource(ORE, 1);
        right.getProduction().addFixedResource(CLAY, 1);
        right.getPublicProduction().addFixedResource(ORE, 1);
        right.getPublicProduction().addFixedResource(CLAY, 1);

        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = TestUtils.createResources(ORE, CLAY);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(4, BestPriceCalculator.bestPrice(resources, table, 2));

        ResourceTransactions oreAndClayLeft = TestUtils.createTransactions(LEFT_PLAYER, ORE, CLAY);
        ResourceTransactions clayRight = TestUtils.createTransactions(RIGHT_PLAYER, CLAY);
        assertEquals(clayRight, BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(oreAndClayLeft, BestPriceCalculator.bestSolution(resources, table, 2));
    }
}

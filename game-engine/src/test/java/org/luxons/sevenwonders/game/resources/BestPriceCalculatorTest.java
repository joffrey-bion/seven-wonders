package org.luxons.sevenwonders.game.resources;

import java.util.Arrays;

import org.junit.Test;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

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
        Table table = TestUtilsKt.testTable(3);
        Resources resources = new Resources();
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 0));
    }

    @Test
    public void bestPrice_fixedResources_defaultCost() {
        Board left = TestUtilsKt.testBoard(STONE);
        Board main = TestUtilsKt.testBoard(STONE);
        Board right = TestUtilsKt.testBoard(WOOD);
        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = TestUtilsKt.createResources(STONE, STONE);
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(4, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 2));

        ResourceTransaction stoneLeftSingle = TestUtilsKt.createTransaction(LEFT_PLAYER, STONE);
        ResourceTransaction stoneRightSingle = TestUtilsKt.createTransaction(RIGHT_PLAYER, STONE);

        ResourceTransactions stoneLeft = TestUtilsKt.createTransactions(stoneLeftSingle);
        ResourceTransactions stoneRight = TestUtilsKt.createTransactions(stoneRightSingle);
        ResourceTransactions stoneLeftAndRight = TestUtilsKt.createTransactions(stoneLeftSingle, stoneRightSingle);

        assertEquals(stoneLeft, BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(stoneLeftAndRight, BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(stoneRight, BestPriceCalculator.bestSolution(resources, table, 2));
    }

    @Test
    public void bestPrice_fixedResources_overridenCost() {
        Board main = TestUtilsKt.testBoard(STONE);
        main.getTradingRules().setCost(WOOD, RIGHT_PLAYER, 1);

        Board left = TestUtilsKt.testBoard(WOOD);
        Board right = TestUtilsKt.testBoard(WOOD);
        Board opposite = TestUtilsKt.testBoard(GLASS);
        Table table = new Table(Arrays.asList(main, right, opposite, left));

        Resources resources = TestUtilsKt.createResources(WOOD);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 2));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 3));

        ResourceTransactions woodLeft = TestUtilsKt.createTransactions(LEFT_PLAYER, WOOD);
        ResourceTransactions woodRight = TestUtilsKt.createTransactions(RIGHT_PLAYER, WOOD);
        assertEquals(woodRight, BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(woodLeft, BestPriceCalculator.bestSolution(resources, table, 2));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 3));
    }

    @Test
    public void bestPrice_mixedResources_overridenCost() {
        Board left = TestUtilsKt.testBoard(WOOD);

        Board main = TestUtilsKt.testBoard(STONE);
        main.getTradingRules().setCost(WOOD, RIGHT_PLAYER, 1);

        Board right = TestUtilsKt.testBoard(ORE);
        right.getProduction().addChoice(WOOD, CLAY);
        right.getPublicProduction().addChoice(WOOD, CLAY);

        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = TestUtilsKt.createResources(WOOD);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 2));

        ResourceTransactions woodRight = TestUtilsKt.createTransactions(RIGHT_PLAYER, WOOD);

        assertEquals(woodRight, BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 2));
    }

    @Test
    public void bestPrice_chooseCheapest() {
        Board left = TestUtilsKt.testBoard(WOOD);

        Board main = TestUtilsKt.testBoard(WOOD);
        main.getProduction().addChoice(CLAY, ORE);
        main.getTradingRules().setCost(CLAY, RIGHT_PLAYER, 1);

        Board right = TestUtilsKt.testBoard(WOOD);
        right.getProduction().addFixedResource(ORE, 1);
        right.getProduction().addFixedResource(CLAY, 1);
        right.getPublicProduction().addFixedResource(ORE, 1);
        right.getPublicProduction().addFixedResource(CLAY, 1);

        Table table = new Table(Arrays.asList(main, right, left));

        Resources resources = TestUtilsKt.createResources(ORE, CLAY);
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0));
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1));
        assertEquals(4, BestPriceCalculator.bestPrice(resources, table, 2));

        ResourceTransactions oreAndClayLeft = TestUtilsKt.createTransactions(LEFT_PLAYER, ORE, CLAY);
        ResourceTransactions clayRight = TestUtilsKt.createTransactions(RIGHT_PLAYER, CLAY);
        assertEquals(clayRight, BestPriceCalculator.bestSolution(resources, table, 0));
        assertEquals(new ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 1));
        assertEquals(oreAndClayLeft, BestPriceCalculator.bestSolution(resources, table, 2));
    }
}

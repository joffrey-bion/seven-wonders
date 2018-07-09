package org.luxons.sevenwonders.game.resources

import java.util.Arrays

import org.junit.Test
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.test.*

import org.junit.Assert.assertEquals
import org.luxons.sevenwonders.game.resources.Provider.LEFT_PLAYER
import org.luxons.sevenwonders.game.resources.Provider.RIGHT_PLAYER
import org.luxons.sevenwonders.game.resources.ResourceType.CLAY
import org.luxons.sevenwonders.game.resources.ResourceType.GLASS
import org.luxons.sevenwonders.game.resources.ResourceType.ORE
import org.luxons.sevenwonders.game.resources.ResourceType.STONE
import org.luxons.sevenwonders.game.resources.ResourceType.WOOD

class BestPriceCalculatorTest {

    @Test
    fun bestPrice_0forEmptyResources() {
        val table = testTable(3)
        val resources = Resources()
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 0).toLong())
        assertEquals(ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 0))
    }

    @Test
    fun bestPrice_fixedResources_defaultCost() {
        val left = testBoard(STONE)
        val main = testBoard(STONE)
        val right = testBoard(WOOD)
        val table = Table(Arrays.asList(main, right, left))

        val resources = createResources(STONE, STONE)
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 0).toLong())
        assertEquals(4, BestPriceCalculator.bestPrice(resources, table, 1).toLong())
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 2).toLong())

        val stoneLeftSingle = createTransaction(LEFT_PLAYER, STONE)
        val stoneRightSingle = createTransaction(RIGHT_PLAYER, STONE)

        val stoneLeft = createTransactions(stoneLeftSingle)
        val stoneRight = createTransactions(stoneRightSingle)
        val stoneLeftAndRight = createTransactions(stoneLeftSingle, stoneRightSingle)

        assertEquals(stoneLeft, BestPriceCalculator.bestSolution(resources, table, 0))
        assertEquals(stoneLeftAndRight, BestPriceCalculator.bestSolution(resources, table, 1))
        assertEquals(stoneRight, BestPriceCalculator.bestSolution(resources, table, 2))
    }

    @Test
    fun bestPrice_fixedResources_overridenCost() {
        val main = testBoard(STONE)
        main.tradingRules.setCost(WOOD, RIGHT_PLAYER, 1)

        val left = testBoard(WOOD)
        val right = testBoard(WOOD)
        val opposite = testBoard(GLASS)
        val table = Table(Arrays.asList(main, right, opposite, left))

        val resources = createResources(WOOD)
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0).toLong())
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1).toLong())
        assertEquals(2, BestPriceCalculator.bestPrice(resources, table, 2).toLong())
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 3).toLong())

        val woodLeft = createTransactions(LEFT_PLAYER, WOOD)
        val woodRight = createTransactions(RIGHT_PLAYER, WOOD)
        assertEquals(woodRight, BestPriceCalculator.bestSolution(resources, table, 0))
        assertEquals(ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 1))
        assertEquals(woodLeft, BestPriceCalculator.bestSolution(resources, table, 2))
        assertEquals(ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 3))
    }

    @Test
    fun bestPrice_mixedResources_overridenCost() {
        val left = testBoard(WOOD)

        val main = testBoard(STONE)
        main.tradingRules.setCost(WOOD, RIGHT_PLAYER, 1)

        val right = testBoard(ORE)
        right.production.addChoice(WOOD, CLAY)
        right.publicProduction.addChoice(WOOD, CLAY)

        val table = Table(Arrays.asList(main, right, left))

        val resources = createResources(WOOD)
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0).toLong())
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1).toLong())
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 2).toLong())

        val woodRight = createTransactions(RIGHT_PLAYER, WOOD)

        assertEquals(woodRight, BestPriceCalculator.bestSolution(resources, table, 0))
        assertEquals(ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 1))
        assertEquals(ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 2))
    }

    @Test
    fun bestPrice_chooseCheapest() {
        val left = testBoard(WOOD)

        val main = testBoard(WOOD)
        main.production.addChoice(CLAY, ORE)
        main.tradingRules.setCost(CLAY, RIGHT_PLAYER, 1)

        val right = testBoard(WOOD)
        right.production.addFixedResource(ORE, 1)
        right.production.addFixedResource(CLAY, 1)
        right.publicProduction.addFixedResource(ORE, 1)
        right.publicProduction.addFixedResource(CLAY, 1)

        val table = Table(Arrays.asList(main, right, left))

        val resources = createResources(ORE, CLAY)
        assertEquals(1, BestPriceCalculator.bestPrice(resources, table, 0).toLong())
        assertEquals(0, BestPriceCalculator.bestPrice(resources, table, 1).toLong())
        assertEquals(4, BestPriceCalculator.bestPrice(resources, table, 2).toLong())

        val oreAndClayLeft = createTransactions(LEFT_PLAYER, ORE, CLAY)
        val clayRight = createTransactions(RIGHT_PLAYER, CLAY)
        assertEquals(clayRight, BestPriceCalculator.bestSolution(resources, table, 0))
        assertEquals(ResourceTransactions(), BestPriceCalculator.bestSolution(resources, table, 1))
        assertEquals(oreAndClayLeft, BestPriceCalculator.bestSolution(resources, table, 2))
    }
}

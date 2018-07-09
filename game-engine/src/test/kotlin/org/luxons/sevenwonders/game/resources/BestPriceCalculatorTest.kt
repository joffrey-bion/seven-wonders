package org.luxons.sevenwonders.game.resources

import org.junit.Assert.assertEquals
import org.junit.Test
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.resources.Provider.LEFT_PLAYER
import org.luxons.sevenwonders.game.resources.Provider.RIGHT_PLAYER
import org.luxons.sevenwonders.game.resources.ResourceType.*
import org.luxons.sevenwonders.game.test.createResources
import org.luxons.sevenwonders.game.test.createTransaction
import org.luxons.sevenwonders.game.test.createTransactions
import org.luxons.sevenwonders.game.test.testBoard
import org.luxons.sevenwonders.game.test.testTable
import java.util.Arrays

class BestPriceCalculatorTest {

    @Test
    fun bestPrice_0forEmptyResources() {
        val table = testTable(3)
        val emptyResources = Resources()
        val emptyTransactions = ResourceTransactions()
        assertEquals(TransactionPlan(0, emptyTransactions), bestSolution(emptyResources, table, 0))
    }

    @Test
    fun bestPrice_fixedResources_defaultCost() {
        val left = testBoard(STONE)
        val main = testBoard(STONE)
        val right = testBoard(WOOD)
        val table = Table(Arrays.asList(main, right, left))

        val resources = createResources(STONE, STONE)

        val stoneLeftSingle = createTransaction(LEFT_PLAYER, STONE)
        val stoneRightSingle = createTransaction(RIGHT_PLAYER, STONE)

        val stoneLeft = createTransactions(stoneLeftSingle)
        val stoneRight = createTransactions(stoneRightSingle)
        val stoneLeftAndRight = createTransactions(stoneLeftSingle, stoneRightSingle)

        assertEquals(TransactionPlan(2, stoneLeft), bestSolution(resources, table, 0))
        assertEquals(TransactionPlan(4, stoneLeftAndRight), bestSolution(resources, table, 1))
        assertEquals(TransactionPlan(2, stoneRight), bestSolution(resources, table, 2))
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

        val woodLeft = createTransactions(LEFT_PLAYER, WOOD)
        val woodRight = createTransactions(RIGHT_PLAYER, WOOD)

        assertEquals(TransactionPlan(1, woodRight), bestSolution(resources, table, 0))
        assertEquals(TransactionPlan(0, ResourceTransactions()), bestSolution(resources, table, 1))
        assertEquals(TransactionPlan(2, woodLeft), bestSolution(resources, table, 2))
        assertEquals(TransactionPlan(0, ResourceTransactions()), bestSolution(resources, table, 3))
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
        val woodRight = createTransactions(RIGHT_PLAYER, WOOD)

        assertEquals(TransactionPlan(1, woodRight), bestSolution(resources, table, 0))
        assertEquals(TransactionPlan(0, ResourceTransactions()), bestSolution(resources, table, 1))
        assertEquals(TransactionPlan(0, ResourceTransactions()), bestSolution(resources, table, 2))
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
        val oreAndClayLeft = createTransactions(LEFT_PLAYER, ORE, CLAY)
        val clayRight = createTransactions(RIGHT_PLAYER, CLAY)

        assertEquals(TransactionPlan(1, clayRight), bestSolution(resources, table, 0))
        assertEquals(TransactionPlan(0, ResourceTransactions()), bestSolution(resources, table, 1))
        assertEquals(TransactionPlan(4, oreAndClayLeft), bestSolution(resources, table, 2))
    }
}

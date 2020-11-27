package org.luxons.sevenwonders.engine.resources

import org.junit.Test
import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.test.createPricedTransaction
import org.luxons.sevenwonders.engine.test.createPricedTransactions
import org.luxons.sevenwonders.engine.test.testBoard
import org.luxons.sevenwonders.engine.test.testTable
import org.luxons.sevenwonders.model.resources.PricedResourceTransactions
import org.luxons.sevenwonders.model.resources.Provider.LEFT_PLAYER
import org.luxons.sevenwonders.model.resources.Provider.RIGHT_PLAYER
import org.luxons.sevenwonders.model.resources.ResourceType.*
import org.luxons.sevenwonders.model.resources.noTransactions
import kotlin.test.assertEquals

class BestPriceCalculatorTest {

    private fun solutions(price: Int, vararg resourceTransactions: PricedResourceTransactions) =
        TransactionPlan(price, setOf(*resourceTransactions))

    @Test
    fun bestPrice_0forEmptyResources() {
        val table = testTable(3)
        val player0 = SimplePlayer(0, table)
        val emptyResources = emptyResources()
        val emptyTransactions = noTransactions()
        assertEquals(solutions(0, emptyTransactions), bestSolution(emptyResources, player0))
    }

    @Test
    fun bestPrice_fixedResources_defaultCost() {
        val left = testBoard(STONE)
        val main = testBoard(STONE)
        val right = testBoard(WOOD)
        val table = Table(listOf(main, right, left))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(STONE, STONE)

        val stoneLeftSingle = createPricedTransaction(LEFT_PLAYER, 2, STONE)
        val stoneRightSingle = createPricedTransaction(RIGHT_PLAYER, 2, STONE)

        val stoneLeft = createPricedTransactions(stoneLeftSingle)
        val stoneRight = createPricedTransactions(stoneRightSingle)
        val stoneLeftAndRight = createPricedTransactions(stoneLeftSingle, stoneRightSingle)

        assertEquals(solutions(2, stoneLeft), bestSolution(resources, player0))
        assertEquals(solutions(4, stoneLeftAndRight), bestSolution(resources, player1))
        assertEquals(solutions(2, stoneRight), bestSolution(resources, player2))
    }

    @Test
    fun bestPrice_fixedResources_overridenCost() {
        val main = testBoard(STONE)
        main.tradingRules.setCost(WOOD, RIGHT_PLAYER, 1)

        val left = testBoard(WOOD)
        val right = testBoard(WOOD)
        val opposite = testBoard(GLASS)
        val table = Table(listOf(main, right, opposite, left))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)
        val player3 = SimplePlayer(3, table)

        val resources = resourcesOf(WOOD)

        val woodLeft = createPricedTransactions(LEFT_PLAYER, 2, WOOD)
        val woodRightDiscounted = createPricedTransactions(RIGHT_PLAYER, 1, WOOD)
        val woodRight = createPricedTransactions(RIGHT_PLAYER, 2, WOOD)

        assertEquals(solutions(1, woodRightDiscounted), bestSolution(resources, player0))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player1))
        assertEquals(solutions(2, woodLeft, woodRight), bestSolution(resources, player2))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player3))
    }

    @Test
    fun bestPrice_mixedResources_overridenCost() {
        val left = testBoard(WOOD)

        val main = testBoard(STONE)
        main.tradingRules.setCost(WOOD, RIGHT_PLAYER, 1)

        val right = testBoard(ORE)
        right.production.addChoice(WOOD, CLAY)
        right.publicProduction.addChoice(WOOD, CLAY)

        val table = Table(listOf(main, right, left))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(WOOD)
        val woodRightDiscounted = createPricedTransactions(RIGHT_PLAYER, 1, WOOD)

        assertEquals(solutions(1, woodRightDiscounted), bestSolution(resources, player0))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player1))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player2))
    }

    @Test
    fun bestPrice_mixedResources_overridenCost_sameMultipleTimes() {
        val left = testBoard(WOOD)

        val main = testBoard(WOOD)
        main.tradingRules.setCost(CLAY, LEFT_PLAYER, 1)
        main.tradingRules.setCost(CLAY, RIGHT_PLAYER, 1)

        main.publicProduction.addFixedResource(STONE, 1)
        main.publicProduction.addChoice(CLAY, ORE)
        main.production.addFixedResource(STONE, 1)
        main.production.addChoice(CLAY, ORE)

        val right = testBoard(CLAY)
        right.publicProduction.addFixedResource(ORE, 1)
        right.publicProduction.addFixedResource(CLAY, 1)
        right.publicProduction.addFixedResource(WOOD, 2)
        right.production.addFixedResource(ORE, 1)
        right.production.addFixedResource(CLAY, 1)
        right.production.addFixedResource(WOOD, 2)

        val table = Table(listOf(main, right, left))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(WOOD, CLAY, CLAY, CLAY)

        val claysRightDiscounted = createPricedTransaction(RIGHT_PLAYER, 2, CLAY, CLAY)
        val claysLeft = createPricedTransaction(LEFT_PLAYER, 4, CLAY, CLAY)
        val clayLeft = createPricedTransaction(LEFT_PLAYER, 2, CLAY)
        val clayRight = createPricedTransaction(RIGHT_PLAYER, 2, CLAY)

        assertEquals(solutions(2, createPricedTransactions(claysRightDiscounted)), bestSolution(resources, player0))
        assertEquals(solutions(2, createPricedTransactions(clayLeft)), bestSolution(resources, player1))
        assertEquals(solutions(6, createPricedTransactions(claysLeft, clayRight)), bestSolution(resources, player2))
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

        val table = Table(listOf(main, right, left))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(ORE, CLAY)
        val oreAndClayLeft = createPricedTransactions(LEFT_PLAYER, 4, ORE, CLAY)
        val clayRightDiscounted = createPricedTransactions(RIGHT_PLAYER, 1, CLAY)

        assertEquals(solutions(1, clayRightDiscounted), bestSolution(resources, player0))
        assertEquals(solutions(0, noTransactions()), bestSolution(resources, player1))
        assertEquals(solutions(4, oreAndClayLeft), bestSolution(resources, player2))
    }
}

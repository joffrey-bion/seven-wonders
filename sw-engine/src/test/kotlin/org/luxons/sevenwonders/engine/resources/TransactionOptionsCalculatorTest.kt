package org.luxons.sevenwonders.engine.resources

import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.test.createPricedTransaction
import org.luxons.sevenwonders.engine.test.createPricedTransactions
import org.luxons.sevenwonders.engine.test.testBoard
import org.luxons.sevenwonders.engine.test.testTable
import org.luxons.sevenwonders.model.resources.Provider.LEFT_PLAYER
import org.luxons.sevenwonders.model.resources.Provider.RIGHT_PLAYER
import org.luxons.sevenwonders.model.resources.ResourceType.*
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.model.resources.singleOptionNoTransactionNeeded
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionOptionsCalculatorTest {

    @Test
    fun transactionOptions_noResourcesRequired_nothingToBuy() {
        val table = testTable(3)
        val player0 = SimplePlayer(0, table)
        val emptyResources = emptyResources()
        assertEquals(singleOptionNoTransactionNeeded(), transactionOptions(emptyResources, player0))
    }

    @Test
    fun transactionOptions_initialResources_defaultCost() {
        val board2 = testBoard(STONE)
        val board0 = testBoard(STONE)
        val board1 = testBoard(WOOD)
        val table = Table(listOf(board0, board1, board2))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(STONE, STONE)

        val stoneLeftSingle = createPricedTransaction(LEFT_PLAYER, 2, STONE)
        val stoneRightSingle = createPricedTransaction(RIGHT_PLAYER, 2, STONE)

        val stoneLeft = createPricedTransactions(stoneLeftSingle)
        val stoneRight = createPricedTransactions(stoneRightSingle)
        val stoneLeftAndRight = createPricedTransactions(stoneLeftSingle, stoneRightSingle)

        assertEquals(listOf(stoneLeft), transactionOptions(resources, player0))
        assertEquals(listOf(stoneLeftAndRight), transactionOptions(resources, player1))
        assertEquals(listOf(stoneRight), transactionOptions(resources, player2))
    }

    @Test
    fun transactionOptions_fixedResources_defaultCost() {
        val board2 = testBoard(WOOD)

        val board0 = testBoard(STONE)
        board0.publicProduction.addFixedResource(CLAY, 1)
        board0.production.addFixedResource(CLAY, 1)

        val board1 = testBoard(ORE)

        val table = Table(listOf(board0, board1, board2))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(STONE, CLAY)

        val stoneAndClayLeft = createPricedTransaction(LEFT_PLAYER, 4, STONE, CLAY)
        val stoneAndClayRight = createPricedTransaction(RIGHT_PLAYER, 4, STONE, CLAY)

        assertEquals(singleOptionNoTransactionNeeded(), transactionOptions(resources, player0))
        assertEquals(listOf(createPricedTransactions(stoneAndClayLeft)), transactionOptions(resources, player1))
        assertEquals(listOf(createPricedTransactions(stoneAndClayRight)), transactionOptions(resources, player2))
    }

    @Test
    fun transactionOptions_fixedResources_overridenCost_cheapestFirst() {
        val board0 = testBoard(STONE)
        board0.tradingRules.setCost(WOOD, RIGHT_PLAYER, 1)

        val board1 = testBoard(WOOD)
        val board2 = testBoard(GLASS)
        val board3 = testBoard(WOOD)

        val table = Table(listOf(board0, board1, board2, board3))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)
        val player3 = SimplePlayer(3, table)

        val resources = resourcesOf(WOOD)

        val woodLeft = createPricedTransactions(LEFT_PLAYER, 2, WOOD)
        val woodRightDiscounted = createPricedTransactions(RIGHT_PLAYER, 1, WOOD)
        val woodRight = createPricedTransactions(RIGHT_PLAYER, 2, WOOD)

        assertEquals(listOf(woodRightDiscounted, woodLeft), transactionOptions(resources, player0))
        assertEquals(listOf(noTransactions()), transactionOptions(resources, player1))
        assertEquals(listOf(woodLeft, woodRight), transactionOptions(resources, player2))
        assertEquals(listOf(noTransactions()), transactionOptions(resources, player3))
    }

    @Test
    fun transactionOptions_choiceResources_overridenCost() {
        val board0 = testBoard(STONE)
        board0.tradingRules.setCost(WOOD, RIGHT_PLAYER, 1)

        val board1 = testBoard(ORE)
        board1.production.addChoice(WOOD, CLAY)
        board1.publicProduction.addChoice(WOOD, CLAY)

        val board2 = testBoard(WOOD)

        val table = Table(listOf(board0, board1, board2))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(WOOD)

        val woodRightDiscounted = createPricedTransactions(RIGHT_PLAYER, 1, WOOD)
        val woodLeft = createPricedTransactions(LEFT_PLAYER, 2, WOOD)

        assertEquals(listOf(woodRightDiscounted, woodLeft), transactionOptions(resources, player0))
        assertEquals(listOf(noTransactions()), transactionOptions(resources, player1))
        assertEquals(listOf(noTransactions()), transactionOptions(resources, player2))
    }

    @Test
    fun transactionOptions_mixedResources_overridenCost() {
        val board0 = testBoard(WOOD)
        board0.production.addChoice(CLAY, ORE)
        board0.tradingRules.setCost(CLAY, RIGHT_PLAYER, 1)

        val board1 = testBoard(WOOD)
        board1.production.addFixedResource(ORE, 1)
        board1.production.addFixedResource(CLAY, 1)
        board1.publicProduction.addFixedResource(ORE, 1)
        board1.publicProduction.addFixedResource(CLAY, 1)

        val board2 = testBoard(WOOD)

        val table = Table(listOf(board0, board1, board2))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(ORE, CLAY)

        val clayRightDiscounted = createPricedTransactions(RIGHT_PLAYER, 1, CLAY)
        val oreAndClayLeft = createPricedTransactions(LEFT_PLAYER, 4, ORE, CLAY)

        assertEquals(listOf(clayRightDiscounted), transactionOptions(resources, player0))
        assertEquals(listOf(noTransactions()), transactionOptions(resources, player1))
        assertEquals(listOf(oreAndClayLeft), transactionOptions(resources, player2))
    }

    @Test
    fun transactionOptions_mixedResources_overridenCost_sameMultipleTimes() {
        val board0 = testBoard(WOOD)
        board0.tradingRules.setCost(CLAY, LEFT_PLAYER, 1)
        board0.tradingRules.setCost(CLAY, RIGHT_PLAYER, 1)

        board0.publicProduction.addFixedResource(STONE, 1)
        board0.publicProduction.addChoice(CLAY, ORE)
        board0.production.addFixedResource(STONE, 1)
        board0.production.addChoice(CLAY, ORE)

        val board1 = testBoard(CLAY)
        board1.publicProduction.addFixedResource(ORE, 1)
        board1.publicProduction.addFixedResource(CLAY, 1)
        board1.publicProduction.addFixedResource(WOOD, 2)
        board1.production.addFixedResource(ORE, 1)
        board1.production.addFixedResource(CLAY, 1)
        board1.production.addFixedResource(WOOD, 2)

        val board2 = testBoard(WOOD)

        val table = Table(listOf(board0, board1, board2))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(WOOD, CLAY, CLAY, CLAY)

        val claysRightDiscounted = createPricedTransaction(RIGHT_PLAYER, 2, CLAY, CLAY)
        val claysLeft = createPricedTransaction(LEFT_PLAYER, 4, CLAY, CLAY)
        val clayLeft = createPricedTransaction(LEFT_PLAYER, 2, CLAY)
        val clayRight = createPricedTransaction(RIGHT_PLAYER, 2, CLAY)

        assertEquals(listOf(createPricedTransactions(claysRightDiscounted)), transactionOptions(resources, player0))
        assertEquals(listOf(createPricedTransactions(clayLeft)), transactionOptions(resources, player1))
        assertEquals(listOf(createPricedTransactions(claysLeft, clayRight)), transactionOptions(resources, player2))
    }

    @Test
    fun transactionOptions_avoidOptionsWithWorsePriceOnBothSides() {
        val board0 = testBoard(STONE)
        board0.tradingRules.setCost(CLAY, LEFT_PLAYER, 1)
        board0.tradingRules.setCost(WOOD, RIGHT_PLAYER, 1)

        val board1 = testBoard(CLAY)
        board1.publicProduction.addFixedResource(WOOD, 1)
        board1.production.addFixedResource(WOOD, 1)

        val board2 = testBoard(WOOD)
        board2.publicProduction.addFixedResource(CLAY, 1)
        board2.production.addFixedResource(CLAY, 1)

        val table = Table(listOf(board0, board1, board2))

        val player0 = SimplePlayer(0, table)
        val player1 = SimplePlayer(1, table)
        val player2 = SimplePlayer(2, table)

        val resources = resourcesOf(WOOD, CLAY)

        val woodAndClayLeft = createPricedTransaction(LEFT_PLAYER, 3, WOOD, CLAY)
        val woodAndClayRight = createPricedTransaction(RIGHT_PLAYER, 3, WOOD, CLAY)
        val clayLeftDiscounted = createPricedTransaction(LEFT_PLAYER, 1, CLAY)
        val woodRightDiscounted = createPricedTransaction(RIGHT_PLAYER, 1, WOOD)

        val everythingLeft = createPricedTransactions(woodAndClayLeft)
        val everythingRight = createPricedTransactions(woodAndClayRight)
        val woodRightClayLeftDiscounted = createPricedTransactions(woodRightDiscounted, clayLeftDiscounted)

        assertEquals(listOf(woodRightClayLeftDiscounted, everythingLeft, everythingRight), transactionOptions(resources, player0))
        assertEquals(singleOptionNoTransactionNeeded(), transactionOptions(resources, player1))
        assertEquals(singleOptionNoTransactionNeeded(), transactionOptions(resources, player2))
    }
}

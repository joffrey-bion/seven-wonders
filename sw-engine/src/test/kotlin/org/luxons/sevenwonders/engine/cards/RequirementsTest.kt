package org.luxons.sevenwonders.engine.cards

import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.resources.emptyResources
import org.luxons.sevenwonders.engine.test.createPricedTransactions
import org.luxons.sevenwonders.engine.test.createRequirements
import org.luxons.sevenwonders.engine.test.singleBoardPlayer
import org.luxons.sevenwonders.engine.test.testBoard
import org.luxons.sevenwonders.model.resources.Provider
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.resources.noTransactions
import kotlin.enums.EnumEntries
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(Theories::class)
class RequirementsTest {

    @Test
    fun getResources_emptyAfterInit() {
        val (_, resources) = Requirements()
        assertTrue(resources.isEmpty())
    }

    @Test
    fun setResources_success() {
        val resources = emptyResources()
        val requirements = Requirements(0, resources)
        assertSame(resources, requirements.resources)
    }

    @Theory
    fun goldRequirement(boardGold: Int, requiredGold: Int) {
        val requirements = Requirements(requiredGold)

        val board = testBoard(ResourceType.CLAY, boardGold)
        val player = singleBoardPlayer(board)

        assertEquals(boardGold >= requiredGold, requirements.areMetWithHelpBy(board, noTransactions()))

        val satisfaction = requirements.assess(player)
        if (boardGold >= requiredGold) {
            if (requiredGold == 0) {
                assertEquals(RequirementsSatisfaction.noRequirements(), satisfaction)
            } else {
                assertEquals(RequirementsSatisfaction.enoughGold(requiredGold), satisfaction)
            }
        } else {
            assertEquals(RequirementsSatisfaction.missingRequiredGold(requiredGold), satisfaction)
        }
    }

    @Theory
    fun resourceRequirement_initialResource(initialResource: ResourceType, requiredResource: ResourceType) {
        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 0)
        val player = singleBoardPlayer(board)

        assertEquals(initialResource == requiredResource, requirements.areMetWithHelpBy(board, noTransactions()))

        if (initialResource == requiredResource) {
            val satisfaction = requirements.assess(player)
            assertEquals(RequirementsSatisfaction.enoughResources(), satisfaction)
        }
    }

    @Theory
    fun resourceRequirement_ownProduction(
        initialResource: ResourceType,
        producedResource: ResourceType,
        requiredResource: ResourceType,
    ) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 0)
        board.production.addFixedResource(producedResource, 1)
        val player = singleBoardPlayer(board)

        assertEquals(producedResource == requiredResource, requirements.areMetWithHelpBy(board, noTransactions()))

        if (producedResource == requiredResource) {
            val satisfaction = requirements.assess(player)
            assertEquals(RequirementsSatisfaction.enoughResources(), satisfaction)
        }
    }

    @Theory
    fun resourceRequirement_boughtResource(
        initialResource: ResourceType,
        boughtResource: ResourceType,
        requiredResource: ResourceType,
    ) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 2)
        val neighbourBoard = testBoard(initialResource, 0)
        neighbourBoard.publicProduction.addFixedResource(boughtResource, 1)
        val table = Table(listOf(board, neighbourBoard))
        val player = SimplePlayer(0, table)

        val resources = createPricedTransactions(Provider.RIGHT_PLAYER, 2, boughtResource)

        val neighbourHasResource = boughtResource == requiredResource
        assertEquals(neighbourHasResource, requirements.areMetWithHelpBy(board, resources))

        val satisfaction = requirements.assess(player)
        if (neighbourHasResource) {
            val transactionOptions = listOf(
                createPricedTransactions(Provider.LEFT_PLAYER, 2, requiredResource),
                createPricedTransactions(Provider.RIGHT_PLAYER, 2, requiredResource),
            )
            assertEquals(RequirementsSatisfaction.metWithHelp(2, transactionOptions), satisfaction)
        } else {
            assertEquals(RequirementsSatisfaction.unavailableResources(), satisfaction)
        }
    }

    @Theory
    fun allTransactionOptionsAreValid(
        initialResource: ResourceType,
        boughtResource: ResourceType,
        requiredResource: ResourceType,
    ) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 1)
        board.tradingRules.setCost(boughtResource, Provider.RIGHT_PLAYER, 1)

        val leftNeighbourBoard = testBoard(initialResource, 0)
        leftNeighbourBoard.publicProduction.addFixedResource(boughtResource, 1)

        val rightNeighbourBoard = testBoard(initialResource, 0)
        rightNeighbourBoard.publicProduction.addFixedResource(boughtResource, 1)

        val table = Table(listOf(leftNeighbourBoard, board, rightNeighbourBoard))
        val player = SimplePlayer(0, table)

        val satisfaction = requirements.assess(player)
        if (satisfaction.satisfied) {
            satisfaction.transactionOptions.forEach {
                assertTrue(
                    requirements.areMetWithHelpBy(board, it),
                    "all transaction options should be valid, but this is not: $it"
                )
            }
        }
    }

    @Theory
    fun pay_boughtResource(initialResource: ResourceType, requiredResource: ResourceType) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 2)
        val neighbourBoard = testBoard(requiredResource, 0)
        val table = Table(listOf(board, neighbourBoard))
        val player = SimplePlayer(0, table)

        val transactions = createPricedTransactions(Provider.RIGHT_PLAYER, 2, requiredResource)

        assertTrue(requirements.areMetWithHelpBy(board, transactions))
        assertTrue(requirements.assess(player).satisfied)

        requirements.pay(player, transactions)

        assertEquals(0, board.gold)
        assertEquals(2, neighbourBoard.gold)
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun goldAmounts(): IntArray = intArrayOf(0, 1, 2, 5)

        @JvmStatic
        @DataPoints
        fun resourceTypes(): EnumEntries<ResourceType> = ResourceType.entries
    }
}

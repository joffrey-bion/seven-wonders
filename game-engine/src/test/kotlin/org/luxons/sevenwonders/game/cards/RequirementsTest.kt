package org.luxons.sevenwonders.game.cards

import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.SimplePlayer
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.resources.Provider
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.emptyResources
import org.luxons.sevenwonders.game.resources.noTransactions
import org.luxons.sevenwonders.game.test.createRequirements
import org.luxons.sevenwonders.game.test.createTransactions
import org.luxons.sevenwonders.game.test.singleBoardPlayer
import org.luxons.sevenwonders.game.test.testBoard
import java.util.Arrays

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

        assertEquals(boardGold >= requiredGold, requirements.areMetWithoutNeighboursBy(board))
        assertEquals(boardGold >= requiredGold, requirements.areMetWithHelpBy(board, noTransactions()))
        assertEquals(boardGold >= requiredGold, requirements.areMetBy(player))
    }

    @Theory
    fun resourceRequirement_initialResource(initialResource: ResourceType, requiredResource: ResourceType) {
        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 0)
        val player = singleBoardPlayer(board)

        assertEquals(initialResource == requiredResource, requirements.areMetWithoutNeighboursBy(board))
        assertEquals(initialResource == requiredResource, requirements.areMetWithHelpBy(board, noTransactions()))

        if (initialResource == requiredResource) {
            assertTrue(requirements.areMetBy(player))
        }
    }

    @Theory
    fun resourceRequirement_ownProduction(
        initialResource: ResourceType,
        producedResource: ResourceType,
        requiredResource: ResourceType
    ) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 0)
        board.production.addFixedResource(producedResource, 1)
        val player = singleBoardPlayer(board)

        assertEquals(producedResource == requiredResource, requirements.areMetWithoutNeighboursBy(board))
        assertEquals(producedResource == requiredResource, requirements.areMetWithHelpBy(board, noTransactions()))

        if (producedResource == requiredResource) {
            assertTrue(requirements.areMetBy(player))
        }
    }

    @Theory
    fun resourceRequirement_boughtResource(
        initialResource: ResourceType,
        boughtResource: ResourceType,
        requiredResource: ResourceType
    ) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 2)
        val neighbourBoard = testBoard(initialResource, 0)
        neighbourBoard.publicProduction.addFixedResource(boughtResource, 1)
        val table = Table(Arrays.asList(board, neighbourBoard))
        val player = SimplePlayer(0, table)

        val resources = createTransactions(Provider.RIGHT_PLAYER, boughtResource)

        assertFalse(requirements.areMetWithoutNeighboursBy(board))
        assertEquals(boughtResource == requiredResource, requirements.areMetWithHelpBy(board, resources))
        assertEquals(boughtResource == requiredResource, requirements.areMetBy(player))
    }

    @Theory
    fun pay_boughtResource(initialResource: ResourceType, requiredResource: ResourceType) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 2)
        val neighbourBoard = testBoard(requiredResource, 0)
        val table = Table(Arrays.asList(board, neighbourBoard))
        val player = SimplePlayer(0, table)

        val transactions = createTransactions(Provider.RIGHT_PLAYER, requiredResource)

        assertFalse(requirements.areMetWithoutNeighboursBy(board))
        assertTrue(requirements.areMetWithHelpBy(board, transactions))
        assertTrue(requirements.areMetBy(player))

        requirements.pay(player, transactions)

        assertEquals(0, board.gold.toLong())
        assertEquals(2, neighbourBoard.gold.toLong())
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun goldAmounts(): IntArray {
            return intArrayOf(0, 1, 2, 5)
        }

        @JvmStatic
        @DataPoints
        fun resourceTypes(): Array<ResourceType> {
            return ResourceType.values()
        }
    }
}

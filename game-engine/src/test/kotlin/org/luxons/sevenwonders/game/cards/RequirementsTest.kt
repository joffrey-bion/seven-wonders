package org.luxons.sevenwonders.game.cards

import java.util.Arrays
import java.util.Collections

import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.resources.Provider
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.test.*

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue

@RunWith(Theories::class)
class RequirementsTest {

    @Test
    fun getResources_emptyAfterInit() {
        val (_, resources) = Requirements()
        assertTrue(resources.isEmpty)
    }

    @Test
    fun setResources_success() {
        val resources = Resources()
        val (_, resources1) = Requirements(0, resources)
        assertSame(resources, resources1)
    }

    @Theory
    fun goldRequirement(boardGold: Int, requiredGold: Int) {
        val requirements = Requirements(requiredGold)

        val board = testBoard(ResourceType.CLAY, boardGold)
        val table = Table(listOf(board))

        assertEquals(boardGold >= requiredGold, requirements.areMetWithoutNeighboursBy(board))
        assertEquals(boardGold >= requiredGold, requirements.areMetWithHelpBy(board, ResourceTransactions()))
        assertEquals(boardGold >= requiredGold, requirements.areMetBy(table, 0))
    }

    @Theory
    fun resourceRequirement_initialResource(initialResource: ResourceType, requiredResource: ResourceType) {
        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 0)
        val table = Table(listOf(board))

        assertEquals(initialResource == requiredResource, requirements.areMetWithoutNeighboursBy(board))
        assertEquals(
            initialResource == requiredResource,
            requirements.areMetWithHelpBy(board, ResourceTransactions())
        )

        if (initialResource == requiredResource) {
            assertTrue(requirements.areMetBy(table, 0))
        }
    }

    @Theory
    fun resourceRequirement_ownProduction(
        initialResource: ResourceType, producedResource: ResourceType,
        requiredResource: ResourceType
    ) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 0)
        board.production.addFixedResource(producedResource, 1)
        val table = Table(listOf(board))

        assertEquals(producedResource == requiredResource, requirements.areMetWithoutNeighboursBy(board))
        assertEquals(
            producedResource == requiredResource,
            requirements.areMetWithHelpBy(board, ResourceTransactions())
        )

        if (producedResource == requiredResource) {
            assertTrue(requirements.areMetBy(table, 0))
        }
    }

    @Theory
    fun resourceRequirement_boughtResource(
        initialResource: ResourceType, boughtResource: ResourceType,
        requiredResource: ResourceType
    ) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 2)
        val neighbourBoard = testBoard(initialResource, 0)
        neighbourBoard.publicProduction.addFixedResource(boughtResource, 1)
        val table = Table(Arrays.asList(board, neighbourBoard))

        val resources = createTransactions(Provider.RIGHT_PLAYER, boughtResource)

        assertFalse(requirements.areMetWithoutNeighboursBy(board))
        assertEquals(boughtResource == requiredResource, requirements.areMetWithHelpBy(board, resources))

        if (boughtResource == requiredResource) {
            assertTrue(requirements.areMetBy(table, 0))
        }
    }

    @Theory
    fun pay_boughtResource(initialResource: ResourceType, requiredResource: ResourceType) {
        assumeTrue(initialResource != requiredResource)

        val requirements = createRequirements(requiredResource)

        val board = testBoard(initialResource, 2)
        val neighbourBoard = testBoard(requiredResource, 0)
        val table = Table(Arrays.asList(board, neighbourBoard))

        val transactions = createTransactions(
            Provider.RIGHT_PLAYER,
            requiredResource
        )

        assertFalse(requirements.areMetWithoutNeighboursBy(board))
        assertTrue(requirements.areMetWithHelpBy(board, transactions))
        assertTrue(requirements.areMetBy(table, 0))

        requirements.pay(table, 0, transactions)

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

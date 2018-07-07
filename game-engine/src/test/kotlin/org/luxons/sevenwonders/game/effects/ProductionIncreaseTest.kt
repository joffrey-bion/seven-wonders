package org.luxons.sevenwonders.game.effects

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.test.*

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue

@RunWith(Theories::class)
class ProductionIncreaseTest {

    @Theory
    fun apply_boardContainsAddedResourceType(
        initialType: ResourceType,
        addedType: ResourceType,
        extraType: ResourceType
    ) {
        val board = testBoard(initialType)
        val effect = ProductionIncrease(fixedProduction(addedType), false)

        effect.apply(board)

        val resources = createResources(initialType, addedType)
        assertTrue(board.production.contains(resources))
        assertFalse(board.publicProduction.contains(resources))

        val moreResources = createResources(initialType, addedType, extraType)
        assertFalse(board.production.contains(moreResources))
        assertFalse(board.publicProduction.contains(moreResources))
    }

    @Theory
    fun apply_boardContainsAddedResourceType_sellable(
        initialType: ResourceType,
        addedType: ResourceType,
        extraType: ResourceType
    ) {
        val board = testBoard(initialType)
        val effect = ProductionIncrease(fixedProduction(addedType), true)

        effect.apply(board)

        val resources = createResources(initialType, addedType)
        assertTrue(board.production.contains(resources))
        assertTrue(board.publicProduction.contains(resources))

        val moreResources = createResources(initialType, addedType, extraType)
        assertFalse(board.production.contains(moreResources))
        assertFalse(board.publicProduction.contains(moreResources))
    }

    @Theory
    fun computePoints_isAlwaysZero(addedType: ResourceType) {
        val effect = ProductionIncrease(fixedProduction(addedType), false)
        val table = testTable(5)
        assertEquals(0, effect.computePoints(table, 0).toLong())
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun resourceTypes(): Array<ResourceType> {
            return ResourceType.values()
        }
    }
}

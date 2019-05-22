package org.luxons.sevenwonders.engine.effects

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.engine.resources.resourcesOf
import org.luxons.sevenwonders.engine.test.fixedProduction
import org.luxons.sevenwonders.engine.test.testBoard
import org.luxons.sevenwonders.engine.test.testTable
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

        effect.applyTo(board)

        val resources = resourcesOf(initialType, addedType)
        assertTrue(board.production.contains(resources))
        assertFalse(board.publicProduction.contains(resources))

        val moreResources = resourcesOf(initialType, addedType, extraType)
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

        effect.applyTo(board)

        val resources = resourcesOf(initialType, addedType)
        assertTrue(board.production.contains(resources))
        assertTrue(board.publicProduction.contains(resources))

        val moreResources = resourcesOf(initialType, addedType, extraType)
        assertFalse(board.production.contains(moreResources))
        assertFalse(board.publicProduction.contains(moreResources))
    }

    @Theory
    fun computePoints_isAlwaysZero(addedType: ResourceType) {
        val effect = ProductionIncrease(fixedProduction(addedType), false)
        val player = SimplePlayer(0, testTable(5))
        assertEquals(0, effect.computePoints(player))
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun resourceTypes(): Array<ResourceType> = ResourceType.values()
    }
}

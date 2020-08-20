package org.luxons.sevenwonders.engine.effects

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.boards.ScienceType
import org.luxons.sevenwonders.engine.test.createScience
import org.luxons.sevenwonders.engine.test.createScienceProgress
import org.luxons.sevenwonders.engine.test.testBoard
import org.luxons.sevenwonders.model.resources.ResourceType
import kotlin.test.assertEquals

@RunWith(Theories::class)
class ScienceProgressTest {

    @Theory
    fun apply_initContainsAddedScience(
        initCompasses: Int,
        initWheels: Int,
        initTablets: Int,
        initJokers: Int,
        compasses: Int,
        wheels: Int,
        tablets: Int,
        jokers: Int
    ) {
        val board = testBoard(ResourceType.ORE)
        val initialScience = createScience(initCompasses, initWheels, initTablets, initJokers)
        board.science.addAll(initialScience)

        val effect = createScienceProgress(compasses, wheels, tablets, jokers)
        effect.applyTo(board)

        assertEquals(initCompasses + compasses, board.science.getQuantity(ScienceType.COMPASS))
        assertEquals(initWheels + wheels, board.science.getQuantity(ScienceType.WHEEL))
        assertEquals(initTablets + tablets, board.science.getQuantity(ScienceType.TABLET))
        assertEquals(initJokers + jokers, board.science.jokers)
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun elementsCount(): IntArray {
            return intArrayOf(0, 1, 2)
        }
    }
}

package org.luxons.sevenwonders.engine.boards

import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.test.createScience
import kotlin.test.assertEquals

@RunWith(Theories::class)
class ScienceTest {

    @Test
    fun addAll_empty() {
        val initial = createScience(3, 4, 5, 1)
        val empty = Science()
        initial.addAll(empty)
        assertEquals(3, initial.getQuantity(ScienceType.COMPASS))
        assertEquals(4, initial.getQuantity(ScienceType.WHEEL))
        assertEquals(5, initial.getQuantity(ScienceType.TABLET))
        assertEquals(1, initial.jokers)
    }

    @Test
    fun addAll_noJoker() {
        val initial = createScience(3, 4, 5, 1)
        val other = createScience(1, 2, 3, 0)
        initial.addAll(other)
        assertEquals(4, initial.getQuantity(ScienceType.COMPASS))
        assertEquals(6, initial.getQuantity(ScienceType.WHEEL))
        assertEquals(8, initial.getQuantity(ScienceType.TABLET))
        assertEquals(1, initial.jokers)
    }

    @Test
    fun addAll_withJokers() {
        val initial = createScience(3, 4, 5, 1)
        val other = createScience(0, 0, 0, 3)
        initial.addAll(other)
        assertEquals(3, initial.getQuantity(ScienceType.COMPASS))
        assertEquals(4, initial.getQuantity(ScienceType.WHEEL))
        assertEquals(5, initial.getQuantity(ScienceType.TABLET))
        assertEquals(4, initial.jokers)
    }

    @Test
    fun addAll_mixed() {
        val initial = createScience(3, 4, 5, 1)
        val other = createScience(1, 2, 3, 4)
        initial.addAll(other)
        assertEquals(4, initial.getQuantity(ScienceType.COMPASS))
        assertEquals(6, initial.getQuantity(ScienceType.WHEEL))
        assertEquals(8, initial.getQuantity(ScienceType.TABLET))
        assertEquals(5, initial.jokers)
    }

    @Theory
    fun computePoints_compassesOnly_noJoker(compasses: Int) {
        val science = createScience(compasses, 0, 0, 0)
        assertEquals(compasses * compasses, science.computePoints())
    }

    @Theory
    fun computePoints_wheelsOnly_noJoker(wheels: Int) {
        val science = createScience(0, wheels, 0, 0)
        assertEquals(wheels * wheels, science.computePoints())
    }

    @Theory
    fun computePoints_tabletsOnly_noJoker(tablets: Int) {
        val science = createScience(0, 0, tablets, 0)
        assertEquals(tablets * tablets, science.computePoints())
    }

    @Theory
    fun computePoints_allSameNoJoker(eachSymbol: Int) {
        val science = createScience(eachSymbol, eachSymbol, eachSymbol, 0)
        assertEquals(3 * eachSymbol * eachSymbol + 7 * eachSymbol, science.computePoints())
    }

    @Theory
    fun computePoints_expectation(expectation: IntArray) {
        val science = createScience(expectation[0], expectation[1], expectation[2], expectation[3])
        assertEquals(expectation[4], science.computePoints())
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun quantitiesWithExpectedPoints(): Array<IntArray> = arrayOf(
            // compasses, wheels, tablets, jokers, expected points
            intArrayOf(0, 0, 0, 1, 1),
            intArrayOf(0, 0, 1, 0, 1),
            intArrayOf(0, 0, 0, 2, 4),
            intArrayOf(0, 0, 1, 1, 4),
            intArrayOf(0, 0, 2, 0, 4),
            intArrayOf(0, 0, 0, 3, 10),
            intArrayOf(0, 0, 1, 2, 10),
            intArrayOf(0, 1, 1, 1, 10),
            intArrayOf(1, 1, 1, 0, 10),
            intArrayOf(0, 0, 0, 4, 16),
            intArrayOf(0, 0, 1, 3, 16),
            intArrayOf(0, 0, 2, 2, 16),
            intArrayOf(0, 0, 3, 1, 16),
            intArrayOf(0, 0, 4, 0, 16)
        )

        @JvmStatic
        @DataPoints
        fun quantitiesDataPoints(): IntArray = intArrayOf(0, 1, 3, 5, 8)
    }
}

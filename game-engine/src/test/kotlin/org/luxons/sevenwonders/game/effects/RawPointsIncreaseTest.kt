package org.luxons.sevenwonders.game.effects

import org.junit.Assert.assertEquals
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.test.testTable

@RunWith(Theories::class)
class RawPointsIncreaseTest {

    @Theory
    fun computePoints_equalsNbOfPoints(points: Int) {
        val rawPointsIncrease = RawPointsIncrease(points)
        val table = testTable(5)
        assertEquals(points.toLong(), rawPointsIncrease.computePoints(table, 0).toLong())
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun points(): IntArray {
            return intArrayOf(0, 1, 2, 3, 5)
        }
    }
}

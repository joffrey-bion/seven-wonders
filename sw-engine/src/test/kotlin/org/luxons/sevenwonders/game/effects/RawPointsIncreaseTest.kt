package org.luxons.sevenwonders.game.effects

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.SimplePlayer
import org.luxons.sevenwonders.game.test.testTable
import kotlin.test.assertEquals

@RunWith(Theories::class)
class RawPointsIncreaseTest {

    @Theory
    fun computePoints_equalsNbOfPoints(points: Int) {
        val rawPointsIncrease = RawPointsIncrease(points)
        val player = SimplePlayer(0, testTable(5))
        assertEquals(points, rawPointsIncrease.computePoints(player))
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun points(): IntArray {
            return intArrayOf(0, 1, 2, 3, 5)
        }
    }
}

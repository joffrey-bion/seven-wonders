package org.luxons.sevenwonders.game.boards

import java.util.HashMap

import org.junit.Rule
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.boards.Military.UnknownAgeException

import org.junit.Assert.assertEquals

@RunWith(Theories::class)
class MilitaryTest {

    @JvmField
    @Rule
    var thrown = ExpectedException.none()

    @Theory
    fun victory_addsCorrectPoints(
        @FromDataPoints("ages") age: Int,
        @FromDataPoints("points") nbPointsPerVictory: Int
    ) {
        val military = createMilitary(age, nbPointsPerVictory, 0)
        val initialPoints = military.totalPoints

        military.victory(age)
        assertEquals((initialPoints + nbPointsPerVictory).toLong(), military.totalPoints.toLong())
    }

    @Theory
    fun victory_failsIfUnknownAge(@FromDataPoints("points") nbPointsPerVictory: Int) {
        val military = createMilitary(0, nbPointsPerVictory, 0)
        thrown.expect(UnknownAgeException::class.java)
        military.victory(1)
    }

    @Theory
    fun defeat_removesCorrectPoints(@FromDataPoints("points") nbPointsLostPerDefeat: Int) {
        val military = createMilitary(0, 0, nbPointsLostPerDefeat)
        val initialPoints = military.totalPoints

        military.defeat()
        assertEquals((initialPoints - nbPointsLostPerDefeat).toLong(), military.totalPoints.toLong())
    }

    companion object {

        @JvmStatic
        @DataPoints("points")
        fun points(): IntArray {
            return intArrayOf(0, 1, 3, 5)
        }

        @JvmStatic
        @DataPoints("ages")
        fun ages(): IntArray {
            return intArrayOf(1, 2, 3)
        }

        private fun createMilitary(age: Int, nbPointsPerVictory: Int, nbPointsPerDefeat: Int): Military {
            val wonPointsPerAge = HashMap<Int, Int>()
            wonPointsPerAge[age] = nbPointsPerVictory
            return Military(nbPointsPerDefeat, wonPointsPerAge)
        }
    }
}

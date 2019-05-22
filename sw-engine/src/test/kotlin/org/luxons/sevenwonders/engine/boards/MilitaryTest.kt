package org.luxons.sevenwonders.engine.boards

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.boards.Military.UnknownAgeException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(Theories::class)
class MilitaryTest {

    @Theory
    fun victory_addsCorrectPoints(
        @FromDataPoints("ages") age: Int,
        @FromDataPoints("points") nbPointsPerVictory: Int
    ) {
        val military = createMilitary(age, nbPointsPerVictory, 0)
        val initialPoints = military.totalPoints

        military.victory(age)
        assertEquals(initialPoints + nbPointsPerVictory, military.totalPoints)
    }

    @Theory
    fun victory_failsIfUnknownAge(@FromDataPoints("points") nbPointsPerVictory: Int) {
        val military = createMilitary(0, nbPointsPerVictory, 0)
        assertFailsWith<UnknownAgeException> {
            military.victory(1)
        }
    }

    @Theory
    fun defeat_removesCorrectPoints(@FromDataPoints("points") nbPointsLostPerDefeat: Int) {
        val military = createMilitary(0, 0, nbPointsLostPerDefeat)
        val initialPoints = military.totalPoints

        military.defeat()
        assertEquals(initialPoints - nbPointsLostPerDefeat, military.totalPoints)
    }

    companion object {

        @JvmStatic
        @DataPoints("points")
        fun points(): IntArray = intArrayOf(0, 1, 3, 5)

        @JvmStatic
        @DataPoints("ages")
        fun ages(): IntArray = intArrayOf(1, 2, 3)

        private fun createMilitary(age: Int, nbPointsPerVictory: Int, nbPointsPerDefeat: Int): Military =
            Military(nbPointsPerDefeat, mapOf(age to nbPointsPerVictory))
    }
}

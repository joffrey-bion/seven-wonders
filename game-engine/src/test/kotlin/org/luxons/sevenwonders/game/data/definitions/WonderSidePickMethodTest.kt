package org.luxons.sevenwonders.game.data.definitions

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.data.WonderSide
import org.luxons.sevenwonders.game.data.WonderSidePickMethod
import java.util.Random

@RunWith(Theories::class)
class WonderSidePickMethodTest {

    private var random: Random? = null

    private var random2: Random? = null

    @Before
    fun setUp() {
        random = Random(123) // starts with TRUE
        random2 = Random(123456) // starts with FALSE
    }

    @Test
    fun pick_allA() {
        var side: WonderSide? = null
        for (i in 0..9) {
            side = WonderSidePickMethod.ALL_A.pickSide(random!!, side)
            assertEquals(WonderSide.A, side)
        }
    }

    @Test
    fun pick_allB() {
        var side: WonderSide? = null
        for (i in 0..9) {
            side = WonderSidePickMethod.ALL_B.pickSide(random!!, side)
            assertEquals(WonderSide.B, side)
        }
    }

    @Test
    fun pick_eachRandom() {
        var side = WonderSidePickMethod.EACH_RANDOM.pickSide(random!!, null)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random!!, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random!!, side)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random!!, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random!!, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random!!, side)
        assertEquals(WonderSide.A, side)
    }

    @Test
    fun pick_eachRandom2() {
        var side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2!!, null)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2!!, side)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2!!, side)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2!!, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2!!, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2!!, side)
        assertEquals(WonderSide.B, side)
    }

    @Theory
    fun pick_allSameRandom_sameAsFirst(firstSide: WonderSide) {
        var side = firstSide
        for (i in 0..9) {
            side = WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random!!, side)
            assertEquals(firstSide, side)
        }
    }

    @Test
    fun pick_allSameRandom_firstIsRandom() {
        assertEquals(WonderSide.A, WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random!!, null))
        assertEquals(WonderSide.B, WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random2!!, null))
    }

    companion object {

        @DataPoints
        fun sides(): Array<WonderSide> {
            return WonderSide.values()
        }
    }
}

package org.luxons.sevenwonders.game.data.definitions

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.api.WonderSidePickMethod
import java.util.Random

@RunWith(Theories::class)
class WonderSidePickMethodTest {

    private lateinit var random: Random

    private lateinit var random2: Random

    @Before
    fun setUp() {
        random = Random(123) // starts with TRUE
        random2 = Random(123456) // starts with FALSE
    }

    @Test
    fun pick_allA() {
        var side: WonderSide? = null
        repeat(10) {
            side = WonderSidePickMethod.ALL_A.pickSide(random, side)
            assertEquals(WonderSide.A, side)
        }
    }

    @Test
    fun pick_allB() {
        var side: WonderSide? = null
        repeat(10) {
            side = WonderSidePickMethod.ALL_B.pickSide(random, side)
            assertEquals(WonderSide.B, side)
        }
    }

    @Test
    fun pick_eachRandom() {
        var side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, null)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side)
        assertEquals(WonderSide.A, side)
    }

    @Test
    fun pick_eachRandom2() {
        var side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, null)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.B, side)
    }

    @Theory
    fun pick_allSameRandom_sameAsFirst(firstSide: WonderSide) {
        var side = firstSide
        repeat(10) {
            side = WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random, side)
            assertEquals(firstSide, side)
        }
    }

    @Test
    fun pick_allSameRandom_firstIsRandom() {
        assertEquals(WonderSide.A, WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random, null))
        assertEquals(WonderSide.B, WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random2, null))
    }

    companion object {

        @DataPoints
        fun sides(): Array<WonderSide> = WonderSide.values()
    }
}

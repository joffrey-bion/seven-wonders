package org.luxons.sevenwonders.game.api

import kotlin.random.Random
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class WonderSidePickMethodTest {

    private lateinit var random: Random

    private lateinit var random2: Random

    @BeforeTest
    fun setUp() {
        random = Random(421) // starts with TRUE
        random2 = Random(42) // starts with FALSE
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
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side)
        assertEquals(WonderSide.B, side)
    }

    @Test
    fun pick_eachRandom2() {
        var side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, null)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.A, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.B, side)
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side)
        assertEquals(WonderSide.B, side)
    }

    @Test
    fun pick_allSameRandom_sameAsFirst() {
        repeat(10) {
            val side = WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random, WonderSide.A)
            assertEquals(WonderSide.A, side)
        }
        repeat(10) {
            val side = WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random, WonderSide.B)
            assertEquals(WonderSide.B, side)
        }
    }

    @Test
    fun pick_allSameRandom_firstIsRandom() {
        assertEquals(WonderSide.A, WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random, null))
        assertEquals(WonderSide.B, WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random2, null))
    }
}

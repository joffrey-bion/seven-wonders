package org.luxons.sevenwonders.game.data.definitions;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class WonderSidePickMethodTest {

    @DataPoints
    public static WonderSide[] sides() {
        return WonderSide.values();
    }

    private Random random;

    private Random random2;

    @Before
    public void setUp() {
        random = new Random(123); // starts with TRUE
        random2 = new Random(123456); // starts with FALSE
    }

    @Test
    public void pick_allA() {
        WonderSide side = null;
        for (int i = 0; i < 10; i++) {
            side = WonderSidePickMethod.ALL_A.pickSide(random, side);
            assertEquals(WonderSide.A, side);
        }
    }

    @Test
    public void pick_allB() {
        WonderSide side = null;
        for (int i = 0; i < 10; i++) {
            side = WonderSidePickMethod.ALL_B.pickSide(random, side);
            assertEquals(WonderSide.B, side);
        }
    }

    @Test
    public void pick_eachRandom() {
        WonderSide side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, null);
        assertEquals(WonderSide.A, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side);
        assertEquals(WonderSide.B, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side);
        assertEquals(WonderSide.A, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side);
        assertEquals(WonderSide.B, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side);
        assertEquals(WonderSide.B, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random, side);
        assertEquals(WonderSide.A, side);
    }

    @Test
    public void pick_eachRandom2() {
        WonderSide side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, null);
        assertEquals(WonderSide.B, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side);
        assertEquals(WonderSide.A, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side);
        assertEquals(WonderSide.A, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side);
        assertEquals(WonderSide.B, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side);
        assertEquals(WonderSide.B, side);
        side = WonderSidePickMethod.EACH_RANDOM.pickSide(random2, side);
        assertEquals(WonderSide.B, side);
    }

    @Theory
    public void pick_allSameRandom_sameAsFirst(WonderSide firstSide) {
        WonderSide side = firstSide;
        for (int i = 0; i < 10; i++) {
            side = WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random, side);
            assertEquals(firstSide, side);
        }
    }

    @Test
    public void pick_allSameRandom_firstIsRandom() {
        assertEquals(WonderSide.A, WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random, null));
        assertEquals(WonderSide.B, WonderSidePickMethod.SAME_RANDOM_FOR_ALL.pickSide(random2, null));
    }
}
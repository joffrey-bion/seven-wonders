package org.luxons.sevenwonders.game.cards;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HandRotationDirectionTest {

    @Test
    public void testAgesDirections() {
        assertEquals(HandRotationDirection.LEFT, HandRotationDirection.forAge(1));
        assertEquals(HandRotationDirection.RIGHT, HandRotationDirection.forAge(2));
        assertEquals(HandRotationDirection.LEFT, HandRotationDirection.forAge(3));
    }
}

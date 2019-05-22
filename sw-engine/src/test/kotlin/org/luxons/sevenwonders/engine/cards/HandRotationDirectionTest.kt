package org.luxons.sevenwonders.engine.cards

import org.junit.Test
import org.luxons.sevenwonders.model.cards.HandRotationDirection
import kotlin.test.assertEquals

class HandRotationDirectionTest {

    @Test
    fun testAgesDirections() {
        assertEquals(HandRotationDirection.LEFT, HandRotationDirection.forAge(1))
        assertEquals(HandRotationDirection.RIGHT, HandRotationDirection.forAge(2))
        assertEquals(HandRotationDirection.LEFT, HandRotationDirection.forAge(3))
    }
}

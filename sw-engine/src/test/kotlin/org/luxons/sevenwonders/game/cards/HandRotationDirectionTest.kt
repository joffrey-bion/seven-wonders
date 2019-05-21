package org.luxons.sevenwonders.game.cards

import org.junit.Test
import org.luxons.sevenwonders.game.api.cards.HandRotationDirection
import kotlin.test.assertEquals

class HandRotationDirectionTest {

    @Test
    fun testAgesDirections() {
        assertEquals(HandRotationDirection.LEFT, HandRotationDirection.forAge(1))
        assertEquals(HandRotationDirection.RIGHT, HandRotationDirection.forAge(2))
        assertEquals(HandRotationDirection.LEFT, HandRotationDirection.forAge(3))
    }
}

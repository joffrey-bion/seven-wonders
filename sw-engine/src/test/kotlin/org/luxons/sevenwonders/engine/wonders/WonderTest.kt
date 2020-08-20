package org.luxons.sevenwonders.engine.wonders

import org.junit.Test
import org.luxons.sevenwonders.engine.test.testWonder
import org.luxons.sevenwonders.model.cards.CardBack
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WonderTest {

    @Test
    fun buildLevel_increasesNbBuiltStages() {
        val wonder = testWonder()
        assertEquals(0, wonder.nbBuiltStages)
        wonder.placeCard(CardBack("img"))
        assertEquals(1, wonder.nbBuiltStages)
        wonder.placeCard(CardBack("img"))
        assertEquals(2, wonder.nbBuiltStages)
        wonder.placeCard(CardBack("img"))
        assertEquals(3, wonder.nbBuiltStages)
    }

    @Test
    fun buildLevel_failsIfFull() {
        val wonder = testWonder()
        wonder.placeCard(CardBack("img"))
        wonder.placeCard(CardBack("img"))
        wonder.placeCard(CardBack("img"))

        assertFailsWith(IllegalStateException::class) {
            wonder.placeCard(CardBack("img"))
        }
    }
}

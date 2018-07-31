package org.luxons.sevenwonders.game.wonders

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.luxons.sevenwonders.game.cards.CardBack
import org.luxons.sevenwonders.game.test.testWonder

class WonderTest {

    @Test
    fun buildLevel_increasesNbBuiltStages() {
        val wonder = testWonder()
        assertEquals(0, wonder.nbBuiltStages.toLong())
        wonder.placeCard(CardBack("img"))
        assertEquals(1, wonder.nbBuiltStages.toLong())
        wonder.placeCard(CardBack("img"))
        assertEquals(2, wonder.nbBuiltStages.toLong())
        wonder.placeCard(CardBack("img"))
        assertEquals(3, wonder.nbBuiltStages.toLong())
    }

    @Test
    fun buildLevel_failsIfFull() {
        val wonder = testWonder()
        wonder.placeCard(CardBack("img"))
        wonder.placeCard(CardBack("img"))
        wonder.placeCard(CardBack("img"))
        try {
            wonder.placeCard(CardBack("img"))
            fail()
        } catch (e: IllegalStateException) {
            // expected exception because there is no 4th level in this wonder
        }
    }
}

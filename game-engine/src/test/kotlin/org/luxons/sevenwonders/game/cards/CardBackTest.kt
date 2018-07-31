package org.luxons.sevenwonders.game.cards

import org.junit.Assert.assertEquals
import org.junit.Test

class CardBackTest {

    @Test
    fun initializedWithImage() {
        val imagePath = "whateverimage.png"
        val (image) = CardBack(imagePath)
        assertEquals(imagePath, image)
    }
}

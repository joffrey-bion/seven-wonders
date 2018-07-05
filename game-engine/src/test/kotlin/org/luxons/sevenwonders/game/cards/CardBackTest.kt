package org.luxons.sevenwonders.game.cards

import org.junit.Test

import org.junit.Assert.assertEquals

class CardBackTest {

    @Test
    fun initializedWithImage() {
        val imagePath = "whateverimage.png"
        val (image) = CardBack(imagePath)
        assertEquals(imagePath, image)
    }
}

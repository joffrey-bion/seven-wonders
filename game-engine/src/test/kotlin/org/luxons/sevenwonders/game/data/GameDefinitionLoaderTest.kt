package org.luxons.sevenwonders.game.data

import org.junit.Assert.assertNotNull
import org.junit.Test

class GameDefinitionLoaderTest {

    @Test
    fun successfulLoad() {
        val gameDefinition = GameDefinitionLoader.gameDefinition
        assertNotNull(gameDefinition)
    }
}

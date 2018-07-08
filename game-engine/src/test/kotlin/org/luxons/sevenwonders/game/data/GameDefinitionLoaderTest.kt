package org.luxons.sevenwonders.game.data

import org.junit.Assert.assertNotNull
import org.junit.Test

class GameDefinitionLoaderTest {

    @Test
    fun successfulLoad() {
        val loader = GameDefinitionLoader()
        val gameDefinition = loader.gameDefinition
        assertNotNull(gameDefinition)
    }
}

package org.luxons.sevenwonders.engine.data

import org.junit.Test
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.wonders.deal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GameDefinitionTest {

    @Test
    fun successfulGameInit() {
        val gameDefinition = GameDefinition.load()
        assertNotNull(gameDefinition)
        assertEquals(3, gameDefinition.minPlayers)
        assertEquals(7, gameDefinition.maxPlayers)

        val wonders = gameDefinition.allWonders.deal(7)
        val game = gameDefinition.createGame(0, wonders, Settings())
        assertNotNull(game)
    }
}

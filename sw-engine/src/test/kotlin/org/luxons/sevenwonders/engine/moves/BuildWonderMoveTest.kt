package org.luxons.sevenwonders.engine.moves

import org.junit.Test
import org.luxons.sevenwonders.engine.PlayerContext
import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.test.createMove
import org.luxons.sevenwonders.engine.test.sampleCards
import org.luxons.sevenwonders.engine.test.testCard
import org.luxons.sevenwonders.engine.test.testSettings
import org.luxons.sevenwonders.engine.test.testTable
import org.luxons.sevenwonders.model.Settings
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

class BuildWonderMoveTest {

    @Test
    fun init_failsWhenCardNotInHand() {
        val table = testTable(3)
        val hand = sampleCards(7)
        val playerContext = PlayerContext(0, table, hand)
        val anotherCard = testCard("Card that is not in the hand")

        assertFailsWith<InvalidMoveException> {
            createMove(playerContext, anotherCard, MoveType.UPGRADE_WONDER)
        }
    }

    @Test
    fun init_failsWhenWonderIsCompletelyBuilt() {
        val settings = testSettings()
        val table = testTable(3, settings)
        val hand = sampleCards(7)

        fillPlayerWonderLevels(settings, table, hand)

        // should fail because the wonder is already full
        assertFailsWith<InvalidMoveException> {
            buildOneWonderLevel(settings, table, hand, 4)
        }
    }

    private fun fillPlayerWonderLevels(settings: Settings, table: Table, hand: List<Card>) {
        try {
            val nbLevels = table.getBoard(0).wonder.stages.size
            repeat(nbLevels) {
                buildOneWonderLevel(settings, table, hand, it)
            }
        } catch (e: InvalidMoveException) {
            fail("Building wonder levels should not fail before being full")
        }
    }

    private fun buildOneWonderLevel(settings: Settings, table: Table, hand: List<Card>, cardIndex: Int) {
        val card = hand[cardIndex]
        val playerContext = PlayerContext(0, table, hand)
        val move = createMove(playerContext, card, MoveType.UPGRADE_WONDER)
        move.place(mutableListOf(), settings)
        move.activate(emptyList(), settings)
    }

    @Test
    fun place_increasesWonderLevel() {
        val settings = testSettings()
        val table = testTable(3, settings)
        val hand = sampleCards(7)
        val cardToUse = hand[0]
        val playerContext = PlayerContext(0, table, hand)
        val move = createMove(playerContext, cardToUse, MoveType.UPGRADE_WONDER)

        val initialStage = table.getBoard(0).wonder.nbBuiltStages

        move.place(mutableListOf(), settings)

        val newStage = table.getBoard(0).wonder.nbBuiltStages

        // we need to see the level increase before activation so that other players
        assertEquals(initialStage + 1, newStage)
    }
}

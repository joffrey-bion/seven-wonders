package org.luxons.sevenwonders.game.moves

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.test.createMove
import org.luxons.sevenwonders.game.test.sampleCards
import org.luxons.sevenwonders.game.test.testCard
import org.luxons.sevenwonders.game.test.testSettings
import org.luxons.sevenwonders.game.test.testTable

class BuildWonderMoveTest {

    @Test(expected = InvalidMoveException::class)
    fun validate_failsWhenCardNotInHand() {
        val table = testTable(3)
        val hand = sampleCards(0, 7)
        val anotherCard = testCard("Card that is not in the hand")
        val move = createMove(0, anotherCard, MoveType.UPGRADE_WONDER)

        move.validate(table, hand)
    }

    @Test(expected = InvalidMoveException::class)
    fun validate_failsWhenWonderIsCompletelyBuilt() {
        val settings = testSettings(3)
        val table = testTable(settings)
        val hand = sampleCards(0, 7)

        fillPlayerWonderLevels(settings, table, hand)

        // should fail because the wonder is already full
        buildOneWonderLevel(settings, table, hand, 4)
    }

    private fun fillPlayerWonderLevels(settings: Settings, table: Table, hand: List<Card>) {
        try {
            val nbLevels = table.getBoard(0).wonder.stages.size
            for (i in 0 until nbLevels) {
                buildOneWonderLevel(settings, table, hand, i)
            }
        } catch (e: InvalidMoveException) {
            fail("Building wonder levels should not fail before being full")
        }
    }

    private fun buildOneWonderLevel(settings: Settings, table: Table, hand: List<Card>, cardIndex: Int) {
        val card = hand[cardIndex]
        val move = createMove(0, card, MoveType.UPGRADE_WONDER)
        move.validate(table, hand)
        move.place(table, mutableListOf(), settings)
        move.activate(table, emptyList(), settings)
    }

    @Test
    fun place_increasesWonderLevel() {
        val settings = testSettings(3)
        val table = testTable(settings)
        val hand = sampleCards(0, 7)
        val cardToUse = hand[0]
        val move = createMove(0, cardToUse, MoveType.UPGRADE_WONDER)
        move.validate(table, hand) // should not fail

        val initialStage = table.getBoard(0).wonder.nbBuiltStages

        move.place(table, mutableListOf(), settings)

        val newStage = table.getBoard(0).wonder.nbBuiltStages

        // we need to see the level increase before activation so that other players
        assertEquals((initialStage + 1).toLong(), newStage.toLong())
    }
}

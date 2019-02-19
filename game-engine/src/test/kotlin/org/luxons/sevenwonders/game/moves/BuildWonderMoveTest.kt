package org.luxons.sevenwonders.game.moves

import org.junit.Test
import org.luxons.sevenwonders.game.PlayerContext
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.test.createMove
import org.luxons.sevenwonders.game.test.sampleCards
import org.luxons.sevenwonders.game.test.testCard
import org.luxons.sevenwonders.game.test.testSettings
import org.luxons.sevenwonders.game.test.testTable
import kotlin.test.assertEquals
import kotlin.test.fail

class BuildWonderMoveTest {

    @Test(expected = InvalidMoveException::class)
    fun init_failsWhenCardNotInHand() {
        val table = testTable(3)
        val hand = sampleCards(7)
        val playerContext = PlayerContext(0, table, hand)
        val anotherCard = testCard("Card that is not in the hand")
        createMove(playerContext, anotherCard, MoveType.UPGRADE_WONDER)
    }

    @Test(expected = InvalidMoveException::class)
    fun init_failsWhenWonderIsCompletelyBuilt() {
        val settings = testSettings(3)
        val table = testTable(settings)
        val hand = sampleCards(7)

        fillPlayerWonderLevels(settings, table, hand)

        // should fail because the wonder is already full
        buildOneWonderLevel(settings, table, hand, 4)
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
        val settings = testSettings(3)
        val table = testTable(settings)
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

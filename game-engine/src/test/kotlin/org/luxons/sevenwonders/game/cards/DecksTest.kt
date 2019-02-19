package org.luxons.sevenwonders.game.cards

import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.cards.Decks.CardNotFoundException
import org.luxons.sevenwonders.game.test.sampleCards
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(Theories::class)
class DecksTest {

    @JvmField
    @Rule
    val thrown: ExpectedException = ExpectedException.none()

    @Test(expected = IllegalArgumentException::class)
    fun getCard_failsOnEmptyNameWhenDeckIsEmpty() {
        val decks = createDecks(0, 0)
        decks.getCard(0, "")
    }

    @Test(expected = IllegalArgumentException::class)
    fun getCard_failsWhenDeckIsEmpty() {
        val decks = createDecks(0, 0)
        decks.getCard(0, "Any name")
    }

    @Test(expected = CardNotFoundException::class)
    fun getCard_failsWhenCardIsNotFound() {
        val decks = createDecks(3, 20)
        decks.getCard(1, "Unknown name")
    }

    @Test
    fun getCard_succeedsWhenCardIsFound() {
        val decks = createDecks(3, 20)
        val (name) = decks.getCard(1, "Test Card 3")
        assertEquals("Test Card 3", name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deal_failsOnZeroPlayers() {
        val decks = createDecks(3, 20)
        decks.deal(1, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deal_failsOnMissingAge() {
        val decks = createDecks(2, 0)
        decks.deal(4, 10)
    }

    @Theory
    fun deal_failsWhenTooFewPlayers(nbPlayers: Int, nbCards: Int) {
        assumeTrue(nbCards % nbPlayers != 0)
        thrown.expect(IllegalArgumentException::class.java)
        val decks = createDecks(1, nbCards)
        decks.deal(1, nbPlayers)
    }

    @Theory
    fun deal_succeedsOnZeroCards(nbPlayers: Int) {
        val decks = createDecks(1, 0)
        val hands = decks.deal(1, nbPlayers)
        repeat(nbPlayers) { i ->
            assertNotNull(hands[i])
            assertTrue(hands[i].isEmpty())
        }
    }

    @Theory
    fun deal_evenDistribution(nbPlayers: Int, nbCardsPerPlayer: Int) {
        val nbCardsPerAge = nbPlayers * nbCardsPerPlayer
        val decks = createDecks(1, nbCardsPerAge)
        val hands = decks.deal(1, nbPlayers)
        repeat(nbPlayers) { i ->
            assertEquals(nbCardsPerPlayer, hands[i].size)
        }
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun dataPoints(): IntArray = intArrayOf(1, 2, 3, 5, 10)

        private fun createDecks(nbAges: Int, nbCardsPerAge: Int): Decks =
            Decks((1..nbAges).map { it to sampleCards(nbCardsPerAge) }.toMap())
    }
}

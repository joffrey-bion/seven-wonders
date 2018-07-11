package org.luxons.sevenwonders.game.cards

import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.SimplePlayer
import org.luxons.sevenwonders.game.test.sampleCards
import org.luxons.sevenwonders.game.test.testTable

@RunWith(Theories::class)
class HandsTest {

    @Test(expected = IndexOutOfBoundsException::class)
    fun get_failsOnMissingPlayer() {
        val hands = createHands(4, 7)
        hands[5]
    }

    @Test
    fun get_retrievesCorrectCards() {
        val hand0 = sampleCards(0, 5)
        val hand1 = sampleCards(5, 10)
        val hands = Hands(listOf(hand0, hand1))
        assertEquals(hand0, hands[0])
        assertEquals(hand1, hands[1])
    }

    @Theory
    fun isEmpty_falseWhenAtLeast1_allSame(
        @FromDataPoints("nbPlayers") nbPlayers: Int,
        @FromDataPoints("nbCardsPerPlayer") nbCardsPerPlayer: Int
    ) {
        assumeTrue(nbCardsPerPlayer >= 1)
        val hands = createHands(nbPlayers, nbCardsPerPlayer)
        assertFalse(hands.isEmpty)
    }

    @Theory
    fun isEmpty_trueWhenAllEmpty(@FromDataPoints("nbPlayers") nbPlayers: Int) {
        val hands = createHands(nbPlayers, 0)
        assertTrue(hands.isEmpty)
    }

    @Theory
    fun maxOneCardRemains_falseWhenAtLeast2_allSame(
        @FromDataPoints("nbPlayers") nbPlayers: Int,
        @FromDataPoints("nbCardsPerPlayer") nbCardsPerPlayer: Int
    ) {
        assumeTrue(nbCardsPerPlayer >= 2)
        val hands = createHands(nbPlayers, nbCardsPerPlayer)
        assertFalse(hands.maxOneCardRemains())
    }

    @Theory
    fun maxOneCardRemains_trueWhenAtMost1_allSame(
        @FromDataPoints("nbPlayers") nbPlayers: Int,
        @FromDataPoints("nbCardsPerPlayer") nbCardsPerPlayer: Int
    ) {
        assumeTrue(nbCardsPerPlayer <= 1)
        val hands = createHands(nbPlayers, nbCardsPerPlayer)
        assertTrue(hands.maxOneCardRemains())
    }

    @Theory
    fun maxOneCardRemains_trueWhenAtMost1_someZero(@FromDataPoints("nbPlayers") nbPlayers: Int) {
        val hands = createHands(nbPlayers, 1)
        hands[0].drop(1)
        assertTrue(hands.maxOneCardRemains())
    }

    @Test
    fun rotate_movesOfCorrectOffset_right() {
        val hands = createHands(3, 7)
        val rotated = hands.rotate(HandRotationDirection.RIGHT)
        assertEquals(rotated[1], hands[0])
        assertEquals(rotated[2], hands[1])
        assertEquals(rotated[0], hands[2])
    }

    @Test
    fun rotate_movesOfCorrectOffset_left() {
        val hands = createHands(3, 7)
        val rotated = hands.rotate(HandRotationDirection.LEFT)
        assertEquals(rotated[2], hands[0])
        assertEquals(rotated[0], hands[1])
        assertEquals(rotated[1], hands[2])
    }

    @Test
    fun createHand_containsAllCards() {
        val hand0 = sampleCards(0, 5)
        val hand1 = sampleCards(5, 10)
        val hands = Hands(listOf(hand0, hand1))

        val table = testTable(2)
        val hand = hands.createHand(SimplePlayer(0, table))

        for (handCard in hand) {
            assertTrue(hand0.contains(handCard.card))
        }
    }

    companion object {

        @JvmStatic
        @DataPoints("nbCardsPerPlayer")
        fun nbCardsPerPlayer(): IntArray {
            return intArrayOf(0, 1, 2, 3, 4, 5, 6, 7)
        }

        @JvmStatic
        @DataPoints("nbPlayers")
        fun nbPlayers(): IntArray {
            return intArrayOf(3, 4, 5, 6, 7)
        }

        private fun createHands(nbPlayers: Int, nbCardsPerPlayer: Int): Hands {
            return sampleCards(0, nbCardsPerPlayer * nbPlayers).deal(nbPlayers)
        }
    }
}

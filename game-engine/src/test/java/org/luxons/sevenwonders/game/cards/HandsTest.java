package org.luxons.sevenwonders.game.cards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.api.HandCard;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.Hands.PlayerIndexOutOfBoundsException;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class HandsTest {

    @DataPoints("nbCardsPerPlayer")
    public static int[] nbCardsPerPlayer() {
        return new int[] {0, 1, 2, 3, 4, 5, 6, 7};
    }

    @DataPoints("nbPlayers")
    public static int[] nbPlayers() {
        return new int[] {3, 4, 5, 6, 7};
    }

    private static Hands createHands(int nbPlayers, int nbCardsPerPlayer) {
        Map<Integer, List<Card>> hands = new HashMap<>();
        for (int p = 0; p < nbPlayers; p++) {
            int firstCardNumber = (p - 1) * nbCardsPerPlayer;
            hands.put(p, TestUtilsKt.createSampleCards(firstCardNumber, nbCardsPerPlayer));
        }
        return new Hands(hands, nbPlayers);
    }

    @Test(expected = PlayerIndexOutOfBoundsException.class)
    public void get_failsOnMissingPlayer() {
        Hands hands = createHands(4, 7);
        hands.get(5);
    }

    @Test
    public void get_retrievesCorrectCards() {
        List<Card> hand0 = TestUtilsKt.createSampleCards(0, 5);
        List<Card> hand1 = TestUtilsKt.createSampleCards(5, 10);
        Map<Integer, List<Card>> handsMap = new HashMap<>();
        handsMap.put(0, hand0);
        handsMap.put(1, hand1);
        Hands hands = new Hands(handsMap, 2);
        assertEquals(hand0, hands.get(0));
        assertEquals(hand1, hands.get(1));
    }

    @Theory
    public void isEmpty_falseWhenAtLeast1_allSame(@FromDataPoints("nbPlayers") int nbPlayers,
            @FromDataPoints("nbCardsPerPlayer") int nbCardsPerPlayer) {
        assumeTrue(nbCardsPerPlayer >= 1);
        Hands hands = createHands(nbPlayers, nbCardsPerPlayer);
        assertFalse(hands.isEmpty());
    }

    @Theory
    public void isEmpty_trueWhenAllEmpty(@FromDataPoints("nbPlayers") int nbPlayers) {
        Hands hands = createHands(nbPlayers, 0);
        assertTrue(hands.isEmpty());
    }

    @Theory
    public void maxOneCardRemains_falseWhenAtLeast2_allSame(@FromDataPoints("nbPlayers") int nbPlayers,
            @FromDataPoints("nbCardsPerPlayer") int nbCardsPerPlayer) {
        assumeTrue(nbCardsPerPlayer >= 2);
        Hands hands = createHands(nbPlayers, nbCardsPerPlayer);
        assertFalse(hands.maxOneCardRemains());
    }

    @Theory
    public void maxOneCardRemains_trueWhenAtMost1_allSame(@FromDataPoints("nbPlayers") int nbPlayers,
            @FromDataPoints("nbCardsPerPlayer") int nbCardsPerPlayer) {
        assumeTrue(nbCardsPerPlayer <= 1);
        Hands hands = createHands(nbPlayers, nbCardsPerPlayer);
        assertTrue(hands.maxOneCardRemains());
    }

    @Theory
    public void maxOneCardRemains_trueWhenAtMost1_someZero(@FromDataPoints("nbPlayers") int nbPlayers) {
        Hands hands = createHands(nbPlayers, 1);
        hands.get(0).remove(0);
        assertTrue(hands.maxOneCardRemains());
    }

    @Theory
    public void gatherAndClear(@FromDataPoints("nbPlayers") int nbPlayers,
            @FromDataPoints("nbCardsPerPlayer") int nbCardsPerPlayer) {
        Hands hands = createHands(nbPlayers, nbCardsPerPlayer);
        List<Card> remainingCards = hands.gatherAndClear();
        assertEquals(nbPlayers * nbCardsPerPlayer, remainingCards.size());
        assertTrue(hands.isEmpty());
    }

    @Test
    public void rotate_movesOfCorrectOffset_right() {
        Hands hands = createHands(3, 7);
        Hands rotated = hands.rotate(HandRotationDirection.RIGHT);
        assertEquals(rotated.get(1), hands.get(0));
        assertEquals(rotated.get(2), hands.get(1));
        assertEquals(rotated.get(0), hands.get(2));
    }

    @Test
    public void rotate_movesOfCorrectOffset_left() {
        Hands hands = createHands(3, 7);
        Hands rotated = hands.rotate(HandRotationDirection.LEFT);
        assertEquals(rotated.get(2), hands.get(0));
        assertEquals(rotated.get(0), hands.get(1));
        assertEquals(rotated.get(1), hands.get(2));
    }

    @Test
    public void createHand_containsAllCards() {
        List<Card> hand0 = TestUtilsKt.createSampleCards(0, 5);
        List<Card> hand1 = TestUtilsKt.createSampleCards(5, 10);
        Map<Integer, List<Card>> handsMap = new HashMap<>();
        handsMap.put(0, hand0);
        handsMap.put(1, hand1);
        Hands hands = new Hands(handsMap, 2);

        Table table = TestUtilsKt.testTable(2);
        List<HandCard> hand = hands.createHand(table, 0);

        for (HandCard handCard : hand) {
            assertTrue(hand0.contains(handCard.getCard()));
        }
    }
}

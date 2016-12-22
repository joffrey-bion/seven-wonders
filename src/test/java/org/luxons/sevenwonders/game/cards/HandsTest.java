package org.luxons.sevenwonders.game.cards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.luxons.sevenwonders.game.cards.Hands.PlayerIndexOutOfBoundsException;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.*;

public class HandsTest {

    private static Hands createHands(int nbPlayers, int nbCardsPerPlayer) {
        Map<Integer, List<Card>> hands = new HashMap<>();
        for (int p = 0; p < nbPlayers; p++) {
            int firstCardNumber = (p - 1) * nbCardsPerPlayer;
            hands.put(p, TestUtils.createSampleCards(firstCardNumber, nbCardsPerPlayer));
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
        List<Card> hand0 = TestUtils.createSampleCards(0, 5);
        List<Card> hand1 = TestUtils.createSampleCards(5, 10);
        Map<Integer, List<Card>> handsMap = new HashMap<>();
        handsMap.put(0, hand0);
        handsMap.put(1, hand1);
        Hands hands = new Hands(handsMap, 2);
        assertEquals(hand0, hands.get(0));
        assertEquals(hand1, hands.get(1));
    }

    @Test
    public void rotate_doesNotMoveWhenOffsetIsZero() {
        Hands hands = createHands(3, 7);
        Hands rotated = hands.rotate(0);
        assertEquals(rotated.get(0), hands.get(0));
        assertEquals(rotated.get(1), hands.get(1));
        assertEquals(rotated.get(2), hands.get(2));
    }

    @Test
    public void rotate_movesOfCorrectOffset_positive() {
        Hands hands = createHands(3, 7);
        Hands rotated = hands.rotate(1);
        assertEquals(rotated.get(1), hands.get(0));
        assertEquals(rotated.get(2), hands.get(1));
        assertEquals(rotated.get(0), hands.get(2));
    }

    @Test
    public void rotate_movesOfCorrectOffset_negative() {
        Hands hands = createHands(3, 7);
        Hands rotated = hands.rotate(-1);
        assertEquals(rotated.get(2), hands.get(0));
        assertEquals(rotated.get(0), hands.get(1));
        assertEquals(rotated.get(1), hands.get(2));
    }

    @Test
    public void rotate_movesOfCorrectOffset_negative2() {
        Hands hands = createHands(3, 7);
        Hands rotated = hands.rotate(-2);
        assertEquals(rotated.get(1), hands.get(0));
        assertEquals(rotated.get(2), hands.get(1));
        assertEquals(rotated.get(0), hands.get(2));
    }
}
package org.luxons.sevenwonders.game.cards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.cards.Decks.CardNotFoundException;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class DecksTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @DataPoints
    public static int[] dataPoints() {
        return new int[] {1, 2, 3, 5, 10};
    }

    private static Decks createDecks(int nbAges, int nbCardsPerAge) {
        Map<Integer, List<Card>> cardsPerAge = new HashMap<>();
        for (int age = 1; age <= nbAges; age++) {
            int firstCardNumber = (age - 1) * nbCardsPerAge;
            cardsPerAge.put(age, TestUtilsKt.createSampleCards(firstCardNumber, nbCardsPerAge));
        }
        return new Decks(cardsPerAge);
    }

    @Test(expected = CardNotFoundException.class)
    public void getCard_failsOnNullNameWhenDeckIsEmpty() {
        Decks decks = createDecks(0, 0);
        decks.getCard(null);
    }

    @Test(expected = CardNotFoundException.class)
    public void getCard_failsOnEmptyNameWhenDeckIsEmpty() {
        Decks decks = createDecks(0, 0);
        decks.getCard("");
    }

    @Test(expected = CardNotFoundException.class)
    public void getCard_failsWhenDeckIsEmpty() {
        Decks decks = createDecks(0, 0);
        decks.getCard("Any name");
    }

    @Test(expected = CardNotFoundException.class)
    public void getCard_failsWhenCardIsNotFound() {
        Decks decks = createDecks(3, 20);
        decks.getCard("Unknown name");
    }

    @Test
    public void getCard_succeedsWhenCardIsFound() {
        Decks decks = createDecks(3, 20);
        Card card = decks.getCard("Test Card 3");
        assertEquals("Test Card 3", card.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deal_failsOnZeroPlayers() {
        Decks decks = createDecks(3, 20);
        decks.deal(1, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deal_failsOnMissingAge() {
        Decks decks = createDecks(2, 0);
        decks.deal(4, 10);
    }

    @Theory
    public void deal_failsWhenTooFewPlayers(int nbPlayers, int nbCards) {
        assumeTrue(nbCards % nbPlayers != 0);
        thrown.expect(IllegalArgumentException.class);
        Decks decks = createDecks(1, nbCards);
        decks.deal(1, nbPlayers);
    }

    @Theory
    public void deal_succeedsOnZeroCards(int nbPlayers) {
        Decks decks = createDecks(1, 0);
        Hands hands = decks.deal(1, nbPlayers);
        for (int i = 0; i < nbPlayers; i++) {
            assertNotNull(hands.get(i));
            assertTrue(hands.get(i).isEmpty());
        }
    }

    @Theory
    public void deal_evenDistribution(int nbPlayers, int nbCardsPerPlayer) {
        int nbCardsPerAge = nbPlayers * nbCardsPerPlayer;
        Decks decks = createDecks(1, nbCardsPerAge);
        Hands hands = decks.deal(1, nbPlayers);
        for (int i = 0; i < nbPlayers; i++) {
            assertEquals(nbCardsPerPlayer, hands.get(i).size());
        }
    }
}

package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.luxons.sevenwonders.game.Decks.CardNotFoundException;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;

import static org.junit.Assert.*;

public class DecksTest {

    private static Decks createDecks(int nbAges, int nbCardsPerAge) {
        Map<Integer, List<Card>> cardsPerAge = new HashMap<>();
        for (int age = 1; age <= nbAges; age++) {
            int firstCardNumber = (age - 1) * nbCardsPerAge;
            cardsPerAge.put(age, createSampleCards(firstCardNumber, nbCardsPerAge));
        }
        return new Decks(cardsPerAge);
    }

    private static List<Card> createSampleCards(int fromIndex, int nbCards) {
        List<Card> sampleCards = new ArrayList<>();
        for (int i = fromIndex; i < fromIndex + nbCards; i++) {
            sampleCards.add(new Card("Test Card " + i, Color.BLUE, null, null, null, null, null));
        }
        return sampleCards;
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

    @Test(expected = IllegalArgumentException.class)
    public void deal_failsWhenTooFewPlayers() {
        Decks decks = createDecks(3, 28);
        decks.deal(1, 3);
    }

    @Test
    public void deal_succeedsOnZeroCards() {
        Decks decks = createDecks(3, 0);
        Map<Integer, List<Card>> hands = decks.deal(1, 10);
        for (List<Card> hand : hands.values()) {
            assertTrue(hand.isEmpty());
        }
    }

    @Test
    public void deal_evenDistribution() {
        int nbCardsPerAge = 12;
        int nbPlayers = 4;
        Decks decks = createDecks(3, nbCardsPerAge);
        Map<Integer, List<Card>> hands = decks.deal(1, nbPlayers);
        for (List<Card> hand : hands.values()) {
            assertEquals(nbCardsPerAge / nbPlayers, hand.size());
        }
    }
}
package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.luxons.sevenwonders.game.cards.Card;

public class Decks {

    private static final int HAND_SIZE = 7;

    private Map<Integer, List<Card>> cardsPerAge = new HashMap<>();

    public Decks(Map<Integer, List<Card>> cardsPerAge) {
        this.cardsPerAge = cardsPerAge;
    }

    Map<Integer, List<Card>> deal(int age, int nbPlayers) {
        List<Card> deck = getDeck(age);
        validateNbCards(deck, nbPlayers);
        return deal(deck, nbPlayers);
    }

    private List<Card> getDeck(int age) {
        List<Card> deck = cardsPerAge.get(age);
        if (deck == null) {
            throw new IllegalArgumentException("No deck found for age " + age);
        }
        return deck;
    }

    private void validateNbCards(List<Card> deck, int nbPlayers) {
        if (nbPlayers * HAND_SIZE != deck.size()) {
            throw new IllegalArgumentException(
                    String.format("%d cards is not the expected number for %d players", deck.size(), nbPlayers));
        }
    }

    private Map<Integer, List<Card>> deal(List<Card> deck, int nbPlayers) {
        Map<Integer, List<Card>> hands = new HashMap<>(nbPlayers);
        for (int i = 0; i < deck.size(); i++) {
            hands.putIfAbsent(i % nbPlayers, new ArrayList<>()).add(deck.get(i));
        }
        return hands;
    }
}

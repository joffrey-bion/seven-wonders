package org.luxons.sevenwonders.game.cards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Decks {

    private Map<Integer, List<Card>> cardsPerAge = new HashMap<>();

    public Decks(Map<Integer, List<Card>> cardsPerAge) {
        this.cardsPerAge = cardsPerAge;
    }

    public Card getCard(String cardName) throws CardNotFoundException {
        return cardsPerAge.values()
                          .stream()
                          .flatMap(List::stream)
                          .filter(c -> c.getName().equals(cardName))
                          .findAny()
                          .orElseThrow(() -> new CardNotFoundException(cardName));
    }

    public Hands deal(int age, int nbPlayers) {
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
        if (nbPlayers == 0) {
            throw new IllegalArgumentException("Cannot deal cards between 0 players");
        }
        if (deck.size() % nbPlayers != 0) {
            throw new IllegalArgumentException(
                    String.format("Cannot deal %d cards evenly between %d players", deck.size(), nbPlayers));
        }
    }

    private Hands deal(List<Card> deck, int nbPlayers) {
        Map<Integer, List<Card>> hands = new HashMap<>(nbPlayers);
        for (int i = 0; i < nbPlayers; i++) {
            hands.put(i, new ArrayList<>());
        }
        for (int i = 0; i < deck.size(); i++) {
            hands.get(i % nbPlayers).add(deck.get(i));
        }
        return new Hands(hands, nbPlayers);
    }

    class CardNotFoundException extends RuntimeException {
        CardNotFoundException(String message) {
            super(message);
        }
    }
}

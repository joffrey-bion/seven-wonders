package org.luxons.sevenwonders.game;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.luxons.sevenwonders.game.cards.Card;

public class Decks {

    private Map<Integer, List<Card>> cardsPerAge = new HashMap<>();

    public Decks(Map<Integer, List<Card>> cardsPerAge) {
        this.cardsPerAge = cardsPerAge;
    }

    public List<Card> getCards(int age) {
        return cardsPerAge.getOrDefault(age, Collections.emptyList());
    }
}

package org.luxons.sevenwonders.game.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class GameData {

    private int nbAges = 3;

    private int minPlayers = 3;

    private int maxPlayers = 7;

    private List<Wonder> wonders = new ArrayList<>();

    private Map<Integer, List<Card>> cardsPerAge = new HashMap<>();

    public int getNbAges() {
        return nbAges;
    }

    public void setNbAges(int nbAges) {
        this.nbAges = nbAges;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public List<Wonder> getWonders() {
        return wonders;
    }

    public void setWonders(List<Wonder> wonders) {
        this.wonders = wonders;
    }

    public List<Card> getCards(int age) {
        return cardsPerAge.getOrDefault(age, Collections.emptyList());
    }

    public void setCards(int age, List<Card> cards) {
        cardsPerAge.put(age, cards);
    }
}

package org.luxons.sevenwonders.game.data;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.wonders.Wonder;

public class GameData {

    private int nbAges = 3;

    private int minPlayers = 3;

    private int maxPlayers = 7;

    private List<Wonder> wonders = new ArrayList<>();

    private List<List<Card>> cardsPerAge = new ArrayList<>();

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
        return cardsPerAge.get(age - 1); // 0-based
    }

    public void setCards(int age, List<Card> cards) {
        cardsPerAge.set(age - 1, cards); // 0-based
    }
}

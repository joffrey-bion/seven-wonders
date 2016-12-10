package org.luxons.sevenwonders.game.data;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.wonders.Wonder;

public class GameData {

    private int minPlayers = 3;

    private int maxPlayers = 7;

    private List<Wonder> wonders = new ArrayList<>();

    private Decks decks;

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

    public Decks getDecks() {
        return decks;
    }

    public void setDecks(Decks decks) {
        this.decks = decks;
    }
}

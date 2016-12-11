package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.wonders.Wonder;

public class GameData {

    private final int minPlayers;

    private final int maxPlayers;

    private List<Wonder> wonders = new ArrayList<>();

    private Decks decks;

    public GameData(int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
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

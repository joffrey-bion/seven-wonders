package org.luxons.sevenwonders.game.data;

@SuppressWarnings("unused") // fields are set by Gson
class GlobalRules {

    private int minPlayers;

    private int maxPlayers;

    int getMinPlayers() {
        return minPlayers;
    }

    int getMaxPlayers() {
        return maxPlayers;
    }
}

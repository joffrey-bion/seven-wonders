package org.luxons.sevenwonders.game.boards;

import org.luxons.sevenwonders.game.Settings;

public class Military {

    private final Settings settings;

    private int nbShields = 0;

    private int totalPoints = 0;

    private int nbDefeatTokens = 0;

    Military(Settings settings) {
        this.settings = settings;
    }

    public int getNbShields() {
        return nbShields;
    }

    public void addShields(int nbShields) {
        this.nbShields += nbShields;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getNbDefeatTokens() {
        return nbDefeatTokens;
    }

    public void victory(int age) {
        int wonPoints = settings.getWonPointsPerVictoryPerAge().get(age);
        totalPoints += wonPoints;
    }

    public void defeat() {
        int lostPoints = settings.getLostPointsPerDefeat();
        totalPoints -= lostPoints;
        nbDefeatTokens++;
    }
}

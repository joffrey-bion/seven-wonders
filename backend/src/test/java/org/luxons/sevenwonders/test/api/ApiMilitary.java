package org.luxons.sevenwonders.test.api;

public class ApiMilitary {

    private int nbShields = 0;

    private int totalPoints = 0;

    private int nbDefeatTokens = 0;

    public int getNbShields() {
        return nbShields;
    }

    public void setNbShields(int nbShields) {
        this.nbShields = nbShields;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getNbDefeatTokens() {
        return nbDefeatTokens;
    }

    public void setNbDefeatTokens(int nbDefeatTokens) {
        this.nbDefeatTokens = nbDefeatTokens;
    }
}

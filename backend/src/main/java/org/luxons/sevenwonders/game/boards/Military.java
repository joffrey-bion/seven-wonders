package org.luxons.sevenwonders.game.boards;

import java.util.Map;

public class Military {

    private final int lostPointsPerDefeat;

    private final Map<Integer, Integer> wonPointsPerVictoryPerAge;

    private int nbShields = 0;

    private int totalPoints = 0;

    private int nbDefeatTokens = 0;

    Military(int lostPointsPerDefeat, Map<Integer, Integer> wonPointsPerVictoryPerAge) {
        this.lostPointsPerDefeat = lostPointsPerDefeat;
        this.wonPointsPerVictoryPerAge = wonPointsPerVictoryPerAge;
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
        Integer wonPoints = wonPointsPerVictoryPerAge.get(age);
        if (wonPoints == null) {
            throw new UnknownAgeException(age);
        }
        totalPoints += wonPoints;
    }

    public void defeat() {
        totalPoints -= lostPointsPerDefeat;
        nbDefeatTokens++;
    }

    static final class UnknownAgeException extends IllegalArgumentException {
        UnknownAgeException(int unknownAge) {
            super(String.valueOf(unknownAge));
        }
    }
}

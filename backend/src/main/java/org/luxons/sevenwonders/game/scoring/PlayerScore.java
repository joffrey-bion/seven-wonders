package org.luxons.sevenwonders.game.scoring;

import java.util.HashMap;

public class PlayerScore extends HashMap<ScoreCategory, Integer> {

    private final int boardGold;

    private int totalPoints = 0;

    public PlayerScore(int boardGold) {
        this.boardGold = boardGold;
    }

    @Override
    public Integer put(ScoreCategory category, Integer points) {
        totalPoints += points;
        return super.put(category, points);
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getBoardGold() {
        return boardGold;
    }
}

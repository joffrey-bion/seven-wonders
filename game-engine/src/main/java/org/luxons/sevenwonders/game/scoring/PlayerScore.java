package org.luxons.sevenwonders.game.scoring;

import java.util.HashMap;
import java.util.Map;

public class PlayerScore {

    private final int boardGold;

    private final Map<ScoreCategory, Integer> scoresByCategory = new HashMap<>();

    private int totalPoints = 0;

    public PlayerScore(int boardGold) {
        this.boardGold = boardGold;
    }

    public Integer put(ScoreCategory category, Integer points) {
        totalPoints += points;
        return scoresByCategory.put(category, points);
    }

    public int getPoints(ScoreCategory category) {
        return scoresByCategory.get(category);
    }

    public Map<ScoreCategory, Integer> getPointsPerCategory() {
        return scoresByCategory;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getBoardGold() {
        return boardGold;
    }
}

package org.luxons.sevenwonders.game.scoring;

import java.util.HashMap;

import org.luxons.sevenwonders.game.Player;

public class PlayerScore extends HashMap<ScoreCategory, Integer> {

    private final Player player;

    private final int boardGold;

    private int totalPoints = 0;

    public PlayerScore(Player player, int boardGold) {
        this.player = player;
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

package org.luxons.sevenwonders.game.scoring;

import java.util.Comparator;
import java.util.PriorityQueue;

public class ScoreBoard {

    private static final Comparator<PlayerScore> comparator = Comparator.comparing(PlayerScore::getTotalPoints)
            .thenComparing(PlayerScore::getBoardGold);

    private PriorityQueue<PlayerScore> scores;

    public ScoreBoard() {
        scores = new PriorityQueue<>(comparator);
    }

    public void add(PlayerScore score) {
        scores.add(score);
    }

    public PriorityQueue<PlayerScore> getScores() {
        return scores;
    }
}

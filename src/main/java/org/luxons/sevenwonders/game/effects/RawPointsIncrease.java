package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;

public class RawPointsIncrease extends EndGameEffect {

    private final int points;

    public int getPoints() {
        return points;
    }

    public RawPointsIncrease(int points) {
        this.points = points;
    }

    @Override
    public int computePoints(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        return points;
    }
}

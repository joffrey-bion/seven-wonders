package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;

public abstract class EndGameEffect implements Effect {

    @Override
    public void apply(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        // EndGameEffects don't do anything when applied to the board, they simply give more points in the end
    }
}

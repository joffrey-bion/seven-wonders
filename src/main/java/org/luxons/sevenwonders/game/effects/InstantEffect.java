package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;

public abstract class InstantEffect extends Effect {

    public int computePoints(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        return 0;
    }
}

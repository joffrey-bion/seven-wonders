package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;

public interface Effect {

    void apply(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard);

    int computePoints(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard);
}

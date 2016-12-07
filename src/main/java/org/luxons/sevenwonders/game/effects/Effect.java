package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;

public abstract class Effect {

    public abstract void apply(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard);

    public abstract int computePoints(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard);
}

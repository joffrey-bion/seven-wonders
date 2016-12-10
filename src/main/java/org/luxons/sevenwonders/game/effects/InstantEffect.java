package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;

public abstract class InstantEffect implements Effect {

    @Override
    public int computePoints(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        // InstantEffects are only important when applied to the board, they don't give extra points in the end
        return 0;
    }
}

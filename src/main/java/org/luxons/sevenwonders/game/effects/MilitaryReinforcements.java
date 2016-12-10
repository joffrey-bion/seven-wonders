package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;

public class MilitaryReinforcements extends InstantEffect {

    private final int count;

    public int getCount() {
        return count;
    }

    public MilitaryReinforcements(int count) {
        this.count = count;
    }

    @Override
    public void apply(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        board.setNbWarSymbols(board.getNbWarSymbols() + count);
    }
}

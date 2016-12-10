package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;

public class SpecialActionTrigger implements Effect {

    private final SpecialAction specialAction;

    public SpecialActionTrigger(SpecialAction specialAction) {
        this.specialAction = specialAction;
    }

    public SpecialAction getSpecialAction() {
        return specialAction;
    }

    @Override
    public void apply(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {

    }

    @Override
    public int computePoints(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        return 0;
    }
}

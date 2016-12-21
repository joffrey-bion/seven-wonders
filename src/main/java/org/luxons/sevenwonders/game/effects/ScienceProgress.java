package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.Science;

public class ScienceProgress extends InstantOwnBoardEffect {

    private Science science;

    public Science getScience() {
        return science;
    }

    public void setScience(Science science) {
        this.science = science;
    }

    @Override
    public void apply(Board board) {
        board.getScience().addAll(science);
    }
}

package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;

public abstract class InstantOwnBoardEffect implements Effect {

    @Override
    public void apply(Table table, int playerIndex) {
        apply(table.getBoard(playerIndex));
    }

    protected abstract void apply(Board board);

    @Override
    public int computePoints(Table table, int playerIndex) {
        // InstantEffects are only important when applied to the board, they don't give extra points in the end
        return 0;
    }
}

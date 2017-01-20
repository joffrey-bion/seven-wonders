package org.luxons.sevenwonders.actions;

import org.luxons.sevenwonders.game.api.PlayerMove;

public class PrepareCardAction {

    private PlayerMove move;

    public PlayerMove getMove() {
        return move;
    }

    public void setMove(PlayerMove move) {
        this.move = move;
    }
}

package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.luxons.sevenwonders.game.api.PlayerMove;

public class PrepareCardAction {

    @NotNull
    private PlayerMove move;

    public PlayerMove getMove() {
        return move;
    }

    public void setMove(PlayerMove move) {
        this.move = move;
    }
}

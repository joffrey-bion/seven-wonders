package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.luxons.sevenwonders.game.api.PlayerMove;

@ApiObject(name = "Prepare Card Action",
           description = "The action to prepare a card during a game.",
           group = "Actions")
public class PrepareCardAction {

    @ApiObjectField
    @NotNull
    private PlayerMove move;

    public PlayerMove getMove() {
        return move;
    }

    public void setMove(PlayerMove move) {
        this.move = move;
    }
}

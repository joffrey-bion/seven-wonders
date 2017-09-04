package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.hildan.livedoc.core.annotations.ApiObject;
import org.hildan.livedoc.core.annotations.ApiObjectField;
import org.luxons.sevenwonders.game.api.PlayerMove;

@ApiObject(name = "Prepare Move Action",
           description = "The action to prepare the next move during a game.",
           group = "Actions")
public class PrepareMoveAction {

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

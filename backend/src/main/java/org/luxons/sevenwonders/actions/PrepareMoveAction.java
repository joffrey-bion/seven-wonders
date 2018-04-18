package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.hildan.livedoc.core.annotations.types.ApiType;
import org.luxons.sevenwonders.doc.Documentation;
import org.luxons.sevenwonders.game.api.PlayerMove;

/**
 * The action to prepare the next move during a game.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
public class PrepareMoveAction {

    @NotNull
    private PlayerMove move;

    /**
     * @return the move to prepare
     */
    public PlayerMove getMove() {
        return move;
    }

    public void setMove(PlayerMove move) {
        this.move = move;
    }
}

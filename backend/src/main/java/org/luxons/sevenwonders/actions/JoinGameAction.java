package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.hildan.livedoc.core.annotations.types.ApiType;
import org.luxons.sevenwonders.doc.Documentation;

/**
 * The action to join a game.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
public class JoinGameAction {

    @NotNull
    private Long gameId;

    /**
     * @return The ID of the game to join
     */
    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}

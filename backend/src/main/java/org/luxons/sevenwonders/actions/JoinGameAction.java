package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.jsondoc.core.annotation.ApiObject;

@ApiObject(name = "Join Game Action",
           description = "The action to join a game.",
           group = "Actions")
public class JoinGameAction {

    @NotNull
    private Long gameId;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}

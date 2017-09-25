package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.hildan.livedoc.core.annotations.ApiObject;

@ApiObject(name = "Join Game Action", description = "The action to join a game.", group = "Actions")
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

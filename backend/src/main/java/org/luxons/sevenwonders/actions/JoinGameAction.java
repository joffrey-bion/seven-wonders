package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

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

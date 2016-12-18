package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class JoinOrCreateGameAction {

    @NotNull
    @Size(min=2, max=30)
    private String gameName;

    @NotNull
    @Size(min=2, max=20)
    private String playerName;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}

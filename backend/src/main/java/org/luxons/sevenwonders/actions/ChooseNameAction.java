package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChooseNameAction {

    @NotNull
    @Size(min=2, max=20)
    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}

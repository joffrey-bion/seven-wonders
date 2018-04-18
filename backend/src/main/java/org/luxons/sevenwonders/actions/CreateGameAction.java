package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hildan.livedoc.core.annotations.types.ApiType;

/**
 * The action to create a game.
 */
@ApiType(group = "Actions")
public class CreateGameAction {

    @NotNull
    @Size(min = 2, max = 30)
    private String gameName;

    /**
     * @return The name of the game to create
     */
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}



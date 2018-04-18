package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hildan.livedoc.core.annotations.types.ApiType;
import org.hildan.livedoc.core.annotations.types.ApiTypeProperty;

/**
 * The action to choose the player's name. This is the first action that should be called.
 */
@ApiType(group = "Actions")
public class ChooseNameAction {

    @NotNull
    @Size(min = 2, max = 20)
    private String playerName;

    /**
     * @return The display name of the player. May contain spaces and special characters.
     */
    @ApiTypeProperty(required = true)
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}

package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject
        (name = "Choose name action",
         description = "The action to choose the player's name. This is the first action that should be called.",
         group = "actions")
public class ChooseNameAction {

    @ApiObjectField(description = "The display name of the player. May contain spaces and special characters.",
                    required = true,
                    format = ".{2,20}")
    @NotNull
    @Size(min = 2, max = 20)
    private String playerName;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}

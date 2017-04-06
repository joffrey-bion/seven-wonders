package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;

@ApiObject(name = "Create Game Action",
           description = "The action to create a game.",
           group = "Actions")
public class CreateGameAction {

    @ApiObjectField
    @NotNull
    @Size(min = 2, max = 30)
    private String gameName;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}

package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.jsondoc.core.annotation.ApiObject;
import org.jsondoc.core.annotation.ApiObjectField;
import org.luxons.sevenwonders.game.api.CustomizableSettings;

@ApiObject(name = "Update Settings Action",
           description = "The action to update the settings of the game. Can only be called in the lobby by the owner"
                   + " of the game.",
           group = "Actions")
public class UpdateSettingsAction {

    @ApiObjectField
    @NotNull
    private CustomizableSettings settings;

    public CustomizableSettings getSettings() {
        return settings;
    }

    public void setSettings(CustomizableSettings settings) {
        this.settings = settings;
    }
}
package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.luxons.sevenwonders.game.api.CustomizableSettings;

public class UpdateSettingsAction {

    @NotNull
    private CustomizableSettings settings;

    public CustomizableSettings getSettings() {
        return settings;
    }

    public void setSettings(CustomizableSettings settings) {
        this.settings = settings;
    }
}

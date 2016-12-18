package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.luxons.sevenwonders.game.Settings;

public class StartGameAction {

    @NotNull
    private Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}

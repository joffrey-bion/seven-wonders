package org.luxons.sevenwonders.actions;

import javax.validation.constraints.NotNull;

import org.hildan.livedoc.core.annotations.types.ApiType;
import org.luxons.sevenwonders.doc.Documentation;
import org.luxons.sevenwonders.game.api.CustomizableSettings;

/**
 * The action to update the settings of the game. Can only be called in the lobby by the owner of the game.
 */
@ApiType(group = Documentation.GROUP_ACTIONS)
public class UpdateSettingsAction {

    @NotNull
    private CustomizableSettings settings;

    /**
     * @return the new values for the settings
     */
    public CustomizableSettings getSettings() {
        return settings;
    }

    public void setSettings(CustomizableSettings settings) {
        this.settings = settings;
    }
}

package org.luxons.sevenwonders.game.data.definitions;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.cards.Requirements;
import org.luxons.sevenwonders.game.wonders.WonderLevel;

public class WonderLevelDefinition implements Definition<WonderLevel> {

    private Requirements requirements;

    private EffectsDefinition effects;

    @Override
    public WonderLevel create(Settings settings) {
        WonderLevel level = new WonderLevel();
        level.setRequirements(requirements);
        level.setEffects(effects.create(settings));
        return level;
    }
}

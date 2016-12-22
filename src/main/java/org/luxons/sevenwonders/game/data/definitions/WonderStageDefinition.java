package org.luxons.sevenwonders.game.data.definitions;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.cards.Requirements;
import org.luxons.sevenwonders.game.wonders.WonderStage;

public class WonderStageDefinition implements Definition<WonderStage> {

    private Requirements requirements;

    private EffectsDefinition effects;

    @Override
    public WonderStage create(Settings settings) {
        WonderStage stage = new WonderStage();
        stage.setRequirements(requirements);
        stage.setEffects(effects.create(settings));
        return stage;
    }
}

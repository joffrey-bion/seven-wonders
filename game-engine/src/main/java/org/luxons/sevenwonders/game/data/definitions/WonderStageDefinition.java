package org.luxons.sevenwonders.game.data.definitions;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.cards.Requirements;
import org.luxons.sevenwonders.game.wonders.WonderStage;

@SuppressWarnings("unused") // the fields are injected by Gson
public class WonderStageDefinition implements Definition<WonderStage> {

    private Requirements requirements;

    private EffectsDefinition effects;

    @Override
    public WonderStage create(Settings settings) {
        return new WonderStage(requirements, effects.create(settings));
    }
}

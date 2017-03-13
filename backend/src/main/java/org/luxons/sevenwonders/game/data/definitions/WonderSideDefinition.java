package org.luxons.sevenwonders.game.data.definitions;

import java.util.List;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.wonders.WonderStage;

// the fields are injected by Gson
@SuppressWarnings("unused,MismatchedQueryAndUpdateOfCollection")
class WonderSideDefinition {

    private ResourceType initialResource;

    private List<WonderStageDefinition> stages;

    private String image;

    ResourceType getInitialResource() {
        return initialResource;
    }

    List<WonderStage> createStages(Settings settings) {
        return stages.stream().map(def -> def.create(settings)).collect(Collectors.toList());
    }

    String getImage() {
        return image;
    }
}

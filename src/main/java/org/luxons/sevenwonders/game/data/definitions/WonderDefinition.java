package org.luxons.sevenwonders.game.data.definitions;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.wonders.Wonder;
import org.luxons.sevenwonders.game.wonders.WonderStage;

public class WonderDefinition implements Definition<Wonder> {

    private String name;

    private Map<WonderSide, WonderSideDefinition> sides;

    @Override
    public Wonder create(Settings settings) {
        Wonder wonder = new Wonder();
        wonder.setName(name);

        WonderSideDefinition wonderSideDef = sides.get(settings.getWonderSide());
        wonder.setInitialResource(wonderSideDef.getInitialResource());
        wonder.setStages(wonderSideDef.createStages(settings));
        wonder.setImage(wonderSideDef.getImage());
        return wonder;
    }

    public static class WonderSideDefinition {

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
}

package org.luxons.sevenwonders.game.data.definitions;

import java.util.List;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.wonders.Wonder;
import org.luxons.sevenwonders.game.wonders.WonderLevel;

public class WonderDefinition implements Definition<Wonder> {

    public String name;

    public WonderSideDefinition a;

    public WonderSideDefinition b;

    @Override
    public Wonder create(Settings settings) {
        Wonder wonder = new Wonder();
        wonder.setName(name);

        WonderSideDefinition wonderSideDef = pickSide(settings);
        wonder.setInitialResource(wonderSideDef.getInitialResource());
        wonder.setLevels(wonderSideDef.createStages(settings));
        wonder.setImage(wonderSideDef.getImage());
        return wonder;
    }

    private WonderSideDefinition pickSide(Settings settings) {
        switch (settings.getWonderSide()){
            case A:
                return a;
            case B:
                return b;
        }
        throw new IllegalArgumentException("Unsupported wonder side " + settings.getWonderSide());
    }

    public static class WonderSideDefinition {

        private ResourceType initialResource;

        private List<WonderLevelDefinition> stages;

        public String image;

        public ResourceType getInitialResource() {
            return initialResource;
        }

        public List<WonderLevel> createStages(Settings settings) {
            return stages.stream().map(def -> def.create(settings)).collect(Collectors.toList());
        }

        public String getImage() {
            return image;
        }
    }
}

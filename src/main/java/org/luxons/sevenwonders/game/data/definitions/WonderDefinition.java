package org.luxons.sevenwonders.game.data.definitions;

import java.util.Map;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.wonders.Wonder;

@SuppressWarnings("unused,MismatchedQueryAndUpdateOfCollection") // the fields are injected by Gson
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

}

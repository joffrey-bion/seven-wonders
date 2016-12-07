package org.luxons.sevenwonders.game.wonders;

import java.util.Arrays;
import java.util.List;

import org.luxons.sevenwonders.game.resources.ResourceType;

public class Wonder {

    private final ResourceType initialResource;

    private final List<WonderLevel> levels;

    public Wonder(ResourceType initialResource, WonderLevel... levels) {
        this.initialResource = initialResource;
        this.levels = Arrays.asList(levels);
    }

    public ResourceType getInitialResource() {
        return initialResource;
    }

    public List<WonderLevel> getLevels() {
        return levels;
    }
}

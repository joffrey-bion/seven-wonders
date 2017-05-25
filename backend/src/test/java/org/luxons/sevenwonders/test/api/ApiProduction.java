package org.luxons.sevenwonders.test.api;

import java.util.Set;

import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;

public class ApiProduction {

    private Resources fixedResources;

    private Set<Set<ResourceType>> alternativeResources;

    public Resources getFixedResources() {
        return fixedResources;
    }

    public void setFixedResources(Resources fixedResources) {
        this.fixedResources = fixedResources;
    }

    public Set<Set<ResourceType>> getAlternativeResources() {
        return alternativeResources;
    }

    public void setAlternativeResources(Set<Set<ResourceType>> alternativeResources) {
        this.alternativeResources = alternativeResources;
    }
}

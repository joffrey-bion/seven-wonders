package org.luxons.sevenwonders.test.api;

import java.util.Map;

import org.luxons.sevenwonders.game.resources.Provider;
import org.luxons.sevenwonders.game.resources.ResourceType;

public class ApiTradingRules {

    private Map<ResourceType, Map<Provider, Integer>> costs;

    public Map<ResourceType, Map<Provider, Integer>> getCosts() {
        return costs;
    }

    public void setCosts(Map<ResourceType, Map<Provider, Integer>> costs) {
        this.costs = costs;
    }
}

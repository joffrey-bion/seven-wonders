package org.luxons.sevenwonders.game.resources;

import java.util.Set;

class ResourcePool {

    private final Set<Set<ResourceType>> choices;

    private final Provider provider;

    private final TradingRules rules;

    ResourcePool(Production production, Provider provider, TradingRules rules) {
        this.choices = production.asChoices();
        this.provider = provider;
        this.rules = rules;
    }

    Set<Set<ResourceType>> getChoices() {
        return choices;
    }

    int getCost(ResourceType type) {
        if (provider == null) {
            return 0;
        }
        return rules.getCost(type, provider);
    }
}

package org.luxons.sevenwonders.game.resources;

import java.util.Set;

class ResourcePool {

    private final Set<Set<ResourceType>> choices;

    private final Provider provider;

    private final TradingRules rules;

    ResourcePool(Provider provider, TradingRules rules, Set<Set<ResourceType>> choices) {
        this.choices = choices;
        this.provider = provider;
        this.rules = rules;
    }

    Set<Set<ResourceType>> getChoices() {
        return choices;
    }

    Provider getProvider() {
        return provider;
    }

    int getCost(ResourceType type) {
        if (provider == null) {
            return 0;
        }
        return rules.getCost(type, provider);
    }
}

package org.luxons.sevenwonders.game.boards;

import java.util.EnumMap;
import java.util.Map;

import org.luxons.sevenwonders.game.resources.ResourceType;

public class TradingRules {

    private final Map<ResourceType, Map<Provider, Integer>> costs = new EnumMap<>(ResourceType.class);

    private final int defaultCost;

    public TradingRules(int defaultCost) {
        this.defaultCost = defaultCost;
    }

    public int getCost(ResourceType type, Provider provider) {
        return costs.computeIfAbsent(type, t -> new EnumMap<>(Provider.class)).getOrDefault(provider, defaultCost);
    }

    public void setCost(ResourceType type, Provider provider, int cost) {
        costs.computeIfAbsent(type, t -> new EnumMap<>(Provider.class)).put(provider, cost);
    }
}

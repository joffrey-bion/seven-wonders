package org.luxons.sevenwonders.game.boards;

import java.util.EnumMap;
import java.util.Map;

import org.luxons.sevenwonders.game.resources.ResourceType;

public class TradingRules {

    private final Map<ResourceType, Map<Neighbour, Integer>> costs = new EnumMap<>(ResourceType.class);

    private final int defaultCost;

    public TradingRules(int defaultCost) {
        this.defaultCost = defaultCost;
    }

    public int getCost(ResourceType type, Neighbour neighbour) {
        return costs.computeIfAbsent(type, t -> new EnumMap<>(Neighbour.class)).getOrDefault(neighbour, defaultCost);
    }

    public void setCost(ResourceType type, Neighbour neighbour, int cost) {
        costs.computeIfAbsent(type, t -> new EnumMap<>(Neighbour.class)).put(neighbour, cost);
    }
}

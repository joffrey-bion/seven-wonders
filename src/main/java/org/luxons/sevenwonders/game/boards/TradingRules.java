package org.luxons.sevenwonders.game.boards;

import java.util.EnumMap;
import java.util.Map;

import org.luxons.sevenwonders.game.RelativePlayerPosition;
import org.luxons.sevenwonders.game.resources.ResourceType;

public class TradingRules {

    private final Map<ResourceType, Map<RelativePlayerPosition, Integer>> costs = new EnumMap<>(ResourceType.class);

    private final int defaultCost;

    public TradingRules(int defaultCost) {
        this.defaultCost = defaultCost;
    }

    public int getCost(ResourceType type, RelativePlayerPosition target) {
        return costs.computeIfAbsent(type, t -> new EnumMap<>(RelativePlayerPosition.class)).getOrDefault(target, defaultCost);
    }

    public void setCost(ResourceType type, RelativePlayerPosition target, int cost) {
        costs.computeIfAbsent(type, t -> new EnumMap<>(RelativePlayerPosition.class)).put(target, cost);
    }
}

package org.luxons.sevenwonders.game.resources;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TradingRules {

    private final Map<ResourceType, Map<Provider, Integer>> costs = new EnumMap<>(ResourceType.class);

    private final int defaultCost;

    public TradingRules(int defaultCost) {
        this.defaultCost = defaultCost;
    }

    private int getCost(ResourceType type, Provider provider) {
        return costs.computeIfAbsent(type, t -> new EnumMap<>(Provider.class)).getOrDefault(provider, defaultCost);
    }

    public void setCost(ResourceType type, Provider provider, int cost) {
        costs.computeIfAbsent(type, t -> new EnumMap<>(Provider.class)).put(provider, cost);
    }

    public int computeCost(List<BoughtResources> boughtResources) {
        return boughtResources.stream().mapToInt(this::computeCost).sum();
    }

    private int computeCost(BoughtResources boughtResources) {
        Resources resources = boughtResources.getResources();
        int total = 0;
        for (Entry<ResourceType, Integer> entry : resources.getQuantities().entrySet()) {
            ResourceType type = entry.getKey();
            int count = entry.getValue();
            total += getCost(type, boughtResources.getProvider()) * count;
        }
        return total;
    }
}

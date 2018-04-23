package org.luxons.sevenwonders.game.resources;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TradingRules {

    private final Map<ResourceType, Map<Provider, Integer>> costs = new EnumMap<>(ResourceType.class);

    private final int defaultCost;

    public TradingRules(int defaultCost) {
        this.defaultCost = defaultCost;
    }

    public Map<ResourceType, Map<Provider, Integer>> getCosts() {
        return costs;
    }

    int getCost(ResourceType type, Provider provider) {
        return costs.computeIfAbsent(type, t -> new EnumMap<>(Provider.class)).getOrDefault(provider, defaultCost);
    }

    public void setCost(ResourceType type, Provider provider, int cost) {
        costs.computeIfAbsent(type, t -> new EnumMap<>(Provider.class)).put(provider, cost);
    }

    public int computeCost(List<BoughtResources> boughtResources) {
        return boughtResources.stream().mapToInt(this::computeCost).sum();
    }

    public int computeCost(BoughtResources boughtResources) {
        Resources resources = boughtResources.getResources();
        Provider provider = boughtResources.getProvider();
        int total = 0;
        for (ResourceType type : ResourceType.values()) {
            int count = resources.getQuantity(type);
            total += getCost(type, provider) * count;
        }
        return total;
    }
}

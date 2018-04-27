package org.luxons.sevenwonders.game.resources;

import java.util.EnumMap;
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

    public int computeCost(ResourceTransactions transactions) {
        return transactions.toTransactions().stream().mapToInt(this::computeCost).sum();
    }

    int computeCost(ResourceTransaction transaction) {
        Resources resources = transaction.getResources();
        Provider provider = transaction.getProvider();
        return computeCost(resources, provider);
    }

    private int computeCost(Resources resources, Provider provider) {
        int total = 0;
        for (ResourceType type : ResourceType.values()) {
            int count = resources.getQuantity(type);
            total += getCost(type, provider) * count;
        }
        return total;
    }
}

package org.luxons.sevenwonders.game.resources;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;

public class BestPriceCalculator {

    private final Resources resources;

    private final List<ResourcePool> pools;

    private BestPriceCalculator(Resources resources, Table table, int playerIndex) {
        this.resources = resources;
        this.pools = createResourcePools(table, playerIndex);
    }

    private static List<ResourcePool> createResourcePools(Table table, int playerIndex) {
        Provider[] providers = Provider.values();
        List<ResourcePool> pools = new ArrayList<>(providers.length + 1);

        Board board = table.getBoard(playerIndex);
        TradingRules rules = board.getTradingRules();
        pools.add(new ResourcePool(board.getProduction(), null, rules));

        for (Provider provider : providers) {
            Board providerBoard = table.getBoard(playerIndex, provider.getBoardPosition());
            ResourcePool pool = new ResourcePool(providerBoard.getPublicProduction(), provider, rules);
            pools.add(pool);
        }
        return pools;
    }

    public static int bestPrice(Resources resources, Table table, int playerIndex) {
        Board board = table.getBoard(playerIndex);
        Resources leftToPay = resources.minus(board.getProduction().getFixedResources());
        return new BestPriceCalculator(leftToPay, table, playerIndex).bestPrice();
    }

    private int bestPrice() {
        if (resources.isEmpty()) {
            return 0;
        }
        int currentMinPrice = Integer.MAX_VALUE;
        for (ResourceType type : ResourceType.values()) {
            if (resources.getQuantity(type) > 0) {
                int minPriceUsingOwnResource = bestPriceWithout(type);
                currentMinPrice = Math.min(currentMinPrice, minPriceUsingOwnResource);
            }
        }
        return currentMinPrice;
    }

    private int bestPriceWithout(ResourceType type) {
        resources.remove(type, 1);
        int currentMinPrice = Integer.MAX_VALUE;
        for (ResourcePool pool : pools) {
            int resCostInPool = pool.getCost(type);
            for (Set<ResourceType> choice : pool.getChoices()) {
                if (choice.contains(type)) {
                    Set<ResourceType> temp = EnumSet.copyOf(choice);
                    choice.clear();
                    currentMinPrice = Math.min(currentMinPrice, bestPrice() + resCostInPool);
                    choice.addAll(temp);
                }
            }
        }
        resources.add(type, 1);
        return currentMinPrice;
    }

    private static class ResourcePool {

        private final Set<Set<ResourceType>> choices;

        private final Provider provider;

        private final TradingRules rules;

        private ResourcePool(Production production, Provider provider, TradingRules rules) {
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
}


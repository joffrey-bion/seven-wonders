package org.luxons.sevenwonders.game.resources;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;

public class BestPriceCalculator {

    private final Resources resourcesLeftToPay;

    private final List<ResourcePool> pools;

    private final List<BoughtResources> boughtResources;

    private int pricePaid;

    private List<BoughtResources> bestSolution;

    private int bestPrice;

    private BestPriceCalculator(Resources resourcesToPay, Table table, int playerIndex) {
        Board board = table.getBoard(playerIndex);
        this.resourcesLeftToPay = resourcesToPay.minus(board.getProduction().getFixedResources());
        this.pools = createResourcePools(table, playerIndex);
        this.boughtResources = new ArrayList<>();
        this.pricePaid = 0;
        this.bestSolution = null;
        this.bestPrice = Integer.MAX_VALUE;
    }

    private static List<ResourcePool> createResourcePools(Table table, int playerIndex) {
        Provider[] providers = Provider.values();
        List<ResourcePool> pools = new ArrayList<>(providers.length + 1);

        Board board = table.getBoard(playerIndex);
        TradingRules rules = board.getTradingRules();
        // we only take alternative resources here, because fixed resources were already removed for optimization
        Set<Set<ResourceType>> ownBoardChoices = board.getProduction().getAlternativeResources();
        pools.add(new ResourcePool(null, rules, ownBoardChoices));

        for (Provider provider : providers) {
            Board providerBoard = table.getBoard(playerIndex, provider.getBoardPosition());
            ResourcePool pool = new ResourcePool(provider, rules, providerBoard.getPublicProduction().asChoices());
            pools.add(pool);
        }
        return pools;
    }

    public static int bestPrice(Resources resources, Table table, int playerIndex) {
        BestPriceCalculator bestPriceCalculator = new BestPriceCalculator(resources, table, playerIndex);
        bestPriceCalculator.computePossibilities();
        return bestPriceCalculator.bestPrice;
    }

    public static List<BoughtResources> bestSolution(Resources resources, Table table, int playerIndex) {
        BestPriceCalculator bestPriceCalculator = new BestPriceCalculator(resources, table, playerIndex);
        bestPriceCalculator.computePossibilities();
        return bestPriceCalculator.bestSolution;
    }

    private void computePossibilities() {
        if (resourcesLeftToPay.isEmpty()) {
            updateBestSolutionIfNeeded();
            return;
        }
        for (ResourceType type : ResourceType.values()) {
            if (resourcesLeftToPay.getQuantity(type) > 0) {
                for (ResourcePool pool : pools) {
                    boolean ownResource = pool.getProvider() == null;
                    if (ownResource) {
                        resourcesLeftToPay.remove(type, 1);
                        computePossibilitiesWhenUsing(type, pool);
                        resourcesLeftToPay.add(type, 1);
                        continue;
                    }
                    BoughtResources boughtRes = new BoughtResources(pool.getProvider(), Resources.of(type));
                    int cost = pool.getCost(type);

                    resourcesLeftToPay.remove(type, 1);
                    boughtResources.add(boughtRes);
                    pricePaid += cost;
                    computePossibilitiesWhenUsing(type, pool);
                    pricePaid -= cost;
                    boughtResources.remove(boughtRes);
                    resourcesLeftToPay.add(type, 1);
                }
            }
        }
    }

    private void computePossibilitiesWhenUsing(ResourceType type, ResourcePool pool) {
        for (Set<ResourceType> choice : pool.getChoices()) {
            if (choice.contains(type)) {
                Set<ResourceType> temp = EnumSet.copyOf(choice);
                choice.clear();
                computePossibilities();
                choice.addAll(temp);
            }
        }
    }

    private void updateBestSolutionIfNeeded() {
        if (pricePaid < bestPrice) {
            bestPrice = pricePaid;
            bestSolution = new ArrayList<>(boughtResources);
        }
    }
}


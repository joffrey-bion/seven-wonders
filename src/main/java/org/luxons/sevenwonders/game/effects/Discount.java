package org.luxons.sevenwonders.game.effects;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.TradingRules;
import org.luxons.sevenwonders.game.resources.ResourceType;

public class Discount extends InstantEffect {

    private final List<ResourceType> resourceTypes = new ArrayList<>();

    private final List<Provider> providers = new ArrayList<>();

    private int discountedPrice = 1;

    public List<ResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public int getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    @Override
    public void apply(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        TradingRules rules = board.getTradingRules();
        for (ResourceType type : resourceTypes) {
            for (Provider provider : providers) {
                rules.setCost(type, provider, discountedPrice);
            }
        }
    }
}

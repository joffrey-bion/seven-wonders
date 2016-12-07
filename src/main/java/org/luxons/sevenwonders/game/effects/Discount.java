package org.luxons.sevenwonders.game.effects;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.boards.Neighbour;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.TradingRules;
import org.luxons.sevenwonders.game.resources.ResourceType;

public class Discount extends InstantEffect {

    private final List<ResourceType> resourceTypes = new ArrayList<>();

    private final List<Neighbour> neighbours = new ArrayList<>();

    private int discountedPrice;

    public List<ResourceType> getResourceTypes() {
        return resourceTypes;
    }

    public List<Neighbour> getNeighbours() {
        return neighbours;
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
            for (Neighbour neighbour : neighbours) {
                rules.setCost(type, neighbour, discountedPrice);
            }
        }
    }
}

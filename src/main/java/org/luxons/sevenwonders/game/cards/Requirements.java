package org.luxons.sevenwonders.game.cards;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.Resources;

public class Requirements {

    private int goldCost;

    private Resources resources = new Resources();

    public int getGoldCost() {
        return goldCost;
    }

    public void setGoldCost(int goldCost) {
        this.goldCost = goldCost;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public boolean isAffordedBy(Board board) {
        return board.getGold() >= goldCost && board.getProduction().contains(resources);
    }

    public void pay(Board board) {
        board.setGold(board.getGold() - goldCost);
    }
}

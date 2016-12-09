package org.luxons.sevenwonders.game.cards;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.Resources;

public class Requirements {

    private int gold;

    private Resources resources = new Resources();

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public boolean isAffordedBy(Board board) {
        return board.getGold() >= gold && board.getProduction().contains(resources);
    }

    public void pay(Board board) {
        board.setGold(board.getGold() - gold);
    }
}

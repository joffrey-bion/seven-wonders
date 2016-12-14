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

    boolean isAffordedBy(Board board) {
        return board.getGold() >= gold && board.getProduction().contains(resources);
    }

    boolean isAffordedBy(Board board, Board left, Board right) {
        if (isAffordedBy(board)) {
            return true;
        }
        // TODO take into account resources buyable from neighbours
        return false;
    }

    void pay(Board board) {
        board.setGold(board.getGold() - gold);
    }
}

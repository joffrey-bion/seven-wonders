package org.luxons.sevenwonders.game.cards;

import java.util.List;

import org.luxons.sevenwonders.game.resources.BoughtResources;
import org.luxons.sevenwonders.game.api.Table;
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

    public boolean isAffordedBy(Table table, int playerIndex) {
        Board board = table.getBoard(playerIndex);
        if (isAffordedBy(board)) {
            return true;
        }
        // TODO take into account resources buyable from neighbours
        return false;
    }

    public boolean isAffordedBy(Table table, int playerIndex, List<BoughtResources> boughtResources) {
        Board board = table.getBoard(playerIndex);
        if (isAffordedBy(board)) {
            return true;
        }
        // TODO take into account resources buyable from neighbours
        return false;
    }

    void pay(Board board) {
        int newBalance = board.getGold() - gold;
        if (newBalance < 0) {
            throw new InsufficientFundsException(board.getGold(), gold);
        }
        board.setGold(newBalance);
    }

    private class InsufficientFundsException extends RuntimeException {
        InsufficientFundsException(int current, int required) {
            super(String.format("Current balance is %d gold, but %d are required", current, required));
        }
    }
}

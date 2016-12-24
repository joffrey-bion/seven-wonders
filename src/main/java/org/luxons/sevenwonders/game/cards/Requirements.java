package org.luxons.sevenwonders.game.cards;

import java.util.List;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.resources.BoughtResources;
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

    public boolean couldBeAffordedBy(Table table, int playerIndex) {
        Board board = table.getBoard(playerIndex);
        if (board.getGold() < gold) {
            return false;
        }
        if (board.getProduction().contains(resources)) {
            return true;
        }
        Resources leftToPay = resources.minus(board.getProduction().getFixedResources());
        // TODO take into account resources buyable from neighbours
        return true;
    }

    public boolean isAffordedBy(Table table, int playerIndex, List<BoughtResources> boughtResources) {
        Board board = table.getBoard(playerIndex);
        if (isAffordedBy(board)) {
            return true;
        }
        int totalPrice = board.getTradingRules().computeCost(boughtResources);
        if (board.getGold() < totalPrice) {
            return false;
        }
        Resources totalBoughtResources = getTotalResources(boughtResources);
        Resources remainingResources = this.resources.minus(totalBoughtResources);
        return board.getProduction().contains(remainingResources);
    }

    private Resources getTotalResources(List<BoughtResources> boughtResources) {
        return boughtResources.stream().map(BoughtResources::getResources).reduce(new Resources(), (r1, r2) -> {
            r1.addAll(r2);
            return r1;
        });
    }

    void pay(Table table, int playerIndex, List<BoughtResources> boughtResources) {
        table.getBoard(playerIndex).removeGold(gold);
        payBoughtResources(table, playerIndex, boughtResources);
    }

    private void payBoughtResources(Table table, int playerIndex, List<BoughtResources> boughtResourcesList) {
        boughtResourcesList.forEach(res -> payBoughtResources(table, playerIndex, res));
    }

    private void payBoughtResources(Table table, int playerIndex, BoughtResources boughtResources) {
        Board board = table.getBoard(playerIndex);
        int price = board.getTradingRules().computeCost(boughtResources);
        board.removeGold(price);
        RelativeBoardPosition providerPosition = boughtResources.getProvider().getBoardPosition();
        Board providerBoard = table.getBoard(playerIndex, providerPosition);
        providerBoard.addGold(price);
    }
}

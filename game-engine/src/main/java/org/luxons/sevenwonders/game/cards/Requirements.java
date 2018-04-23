package org.luxons.sevenwonders.game.cards;

import java.util.List;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.resources.BestPriceCalculator;
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

    /**
     * Returns whether the given board meets these requirements on its own.
     *
     * @param board
     *         the board to check
     *
     * @return true if the given board meets these requirements without any transaction with its neighbours
     */
    boolean areMetWithoutNeighboursBy(Board board) {
        return hasRequiredGold(board) && producesRequiredResources(board);
    }

    /**
     * Returns whether the given board meets these requirements, if the specified resources are bought from neighbours.
     *
     * @param board
     *         the board to check
     * @param boughtResources
     *         the resources the player intends to buy
     *
     * @return true if the given board meets these requirements
     */
    public boolean areMetWithHelpBy(Board board, List<BoughtResources> boughtResources) {
        if (!hasRequiredGold(board, boughtResources)) {
            return false;
        }
        if (producesRequiredResources(board)) {
            return true;
        }
        return producesRequiredResourcesWithHelp(board, boughtResources);
    }

    /**
     * Returns whether the given player's board could meet these requirements, on its own or by buying resources to
     * neighbours.
     *
     * @param table
     *         the current game table
     * @param playerIndex
     *         the index of the player to check
     *
     * @return true if the given player's board could meet these requirements
     */
    boolean couldBeMetBy(Table table, int playerIndex) {
        Board board = table.getBoard(playerIndex);
        if (!hasRequiredGold(board)) {
            return false;
        }
        if (producesRequiredResources(board)) {
            return true;
        }
        return BestPriceCalculator.bestPrice(resources, table, playerIndex) <= board.getGold() - gold;
    }

    private boolean hasRequiredGold(Board board) {
        return board.getGold() >= gold;
    }

    private boolean hasRequiredGold(Board board, List<BoughtResources> boughtResources) {
        int resourcesPrice = board.getTradingRules().computeCost(boughtResources);
        return board.getGold() >= gold + resourcesPrice;
    }

    private boolean producesRequiredResources(Board board) {
        return board.getProduction().contains(resources);
    }

    private boolean producesRequiredResourcesWithHelp(Board board, List<BoughtResources> boughtResources) {
        Resources totalBoughtResources = getTotalResources(boughtResources);
        Resources remainingResources = this.resources.minus(totalBoughtResources);
        return board.getProduction().contains(remainingResources);
    }

    private static Resources getTotalResources(List<BoughtResources> boughtResources) {
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

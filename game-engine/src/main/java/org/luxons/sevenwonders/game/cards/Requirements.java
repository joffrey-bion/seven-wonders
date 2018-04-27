package org.luxons.sevenwonders.game.cards;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.BestPriceCalculator;
import org.luxons.sevenwonders.game.resources.ResourceTransactions;
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
    public boolean areMetWithHelpBy(Board board, ResourceTransactions boughtResources) {
        if (!hasRequiredGold(board, boughtResources)) {
            return false;
        }
        if (producesRequiredResources(board)) {
            return true;
        }
        return producesRequiredResourcesWithHelp(board, boughtResources);
    }

    /**
     * Returns whether the given player's board meets these requirements, either on its own or by buying resources to
     * neighbours.
     *
     * @param table
     *         the current game table
     * @param playerIndex
     *         the index of the player to check
     *
     * @return true if the given player's board could meet these requirements
     */
    boolean areMetBy(Table table, int playerIndex) {
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

    private boolean hasRequiredGold(Board board, ResourceTransactions resourceTransactions) {
        int resourcesPrice = board.getTradingRules().computeCost(resourceTransactions);
        return board.getGold() >= gold + resourcesPrice;
    }

    private boolean producesRequiredResources(Board board) {
        return board.getProduction().contains(resources);
    }

    private boolean producesRequiredResourcesWithHelp(Board board, ResourceTransactions transactions) {
        Resources totalBoughtResources = transactions.asResources();
        Resources remainingResources = this.resources.minus(totalBoughtResources);
        return board.getProduction().contains(remainingResources);
    }

    public void pay(Table table, int playerIndex, ResourceTransactions transactions) {
        table.getBoard(playerIndex).removeGold(gold);
        transactions.execute(table, playerIndex);
    }
}

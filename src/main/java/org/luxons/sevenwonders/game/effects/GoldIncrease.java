package org.luxons.sevenwonders.game.effects;

import org.luxons.sevenwonders.game.boards.Board;

public class GoldIncrease extends InstantEffect {

    private int amount;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        board.setGold(board.getGold() + amount);
    }
}

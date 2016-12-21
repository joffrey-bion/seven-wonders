package org.luxons.sevenwonders.game.effects;

import java.util.Objects;

import org.luxons.sevenwonders.game.boards.Board;

public class GoldIncrease extends InstantOwnBoardEffect {

    private final int amount;

    public int getAmount() {
        return amount;
    }

    public GoldIncrease(int amount) {
        this.amount = amount;
    }

    @Override
    public void apply(Board board) {
        board.setGold(board.getGold() + amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GoldIncrease that = (GoldIncrease)o;
        return amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}

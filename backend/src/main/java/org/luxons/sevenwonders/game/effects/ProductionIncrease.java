package org.luxons.sevenwonders.game.effects;

import java.util.Objects;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.Production;

public class ProductionIncrease extends InstantOwnBoardEffect {

    private Production production = new Production();

    private boolean sellable = true;

    public Production getProduction() {
        return production;
    }

    public void setProduction(Production production) {
        this.production = production;
    }

    public boolean isSellable() {
        return sellable;
    }

    public void setSellable(boolean sellable) {
        this.sellable = sellable;
    }

    @Override
    public void apply(Board board) {
        board.getProduction().addAll(production);
        if (sellable) {
            board.getPublicProduction().addAll(production);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductionIncrease that = (ProductionIncrease)o;
        return Objects.equals(production, that.production);
    }

    @Override
    public int hashCode() {
        return Objects.hash(production);
    }
}

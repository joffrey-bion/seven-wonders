package org.luxons.sevenwonders.game.effects;

import java.util.Objects;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.Production;

public class ProductionIncrease extends InstantEffect {

    private Production production = new Production();

    public Production getProduction() {
        return production;
    }

    public void setProduction(Production production) {
        this.production = production;
    }

    public void apply(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        board.getProduction().addAll(production);
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

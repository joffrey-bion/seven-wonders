package org.luxons.sevenwonders.game.effects;

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
}

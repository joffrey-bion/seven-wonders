package org.luxons.sevenwonders.game.cards;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.effects.Effect;

public class Card {

    private final String name;

    private final Color color;

    private final Requirements requirements;

    private final Effect effect;

    public Card(String name, Color color, Requirements requirements, Effect effect) {
        this.name = name;
        this.color = color;
        this.requirements = requirements;
        this.effect = effect;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public Effect getEffect() {
        return effect;
    }

    public void play(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        board.addCard(this);
        requirements.pay(board);
        effect.apply(board, leftNeighbourBoard, rightNeighbourBoard);
    }

    @Override
    public String toString() {
        return "Card{" + "name='" + name + '\'' + ", color=" + color + ", requirements=" + requirements + ", effect="
                + effect + '}';
    }
}

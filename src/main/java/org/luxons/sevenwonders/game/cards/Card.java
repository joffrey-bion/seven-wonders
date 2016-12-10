package org.luxons.sevenwonders.game.cards;

import java.util.List;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.effects.Effect;

public class Card {

    private final String name;

    private final Color color;

    private final Requirements requirements;

    private final List<Effect> effects;

    public Card(String name, Color color, Requirements requirements, List<Effect> effects) {
        this.name = name;
        this.color = color;
        this.requirements = requirements;
        this.effects = effects;
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

    public List<Effect> getEffects() {
        return effects;
    }

    public void play(Board board, Board leftNeighbourBoard, Board rightNeighbourBoard) {
        // adding the card must be done first, as some effects count the number of cards
        // FIXME this is actually broken as ALL cards in the turn need to be added to the board before any effect
        board.addCard(this);
        requirements.pay(board);
        effects.forEach(e -> e.apply(board, leftNeighbourBoard, rightNeighbourBoard));
    }

    @Override
    public String toString() {
        return "Card{" + "name='" + name + '\'' + ", color=" + color + ", requirements=" + requirements + ", effects="
                + effects + '}';
    }
}

package org.luxons.sevenwonders.game.cards;

import java.util.List;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.effects.Effect;

public class Card {

    private final String name;

    private final Color color;

    private final Requirements requirements;

    private final String chainParent;

    private final List<Effect> effects;

    private final List<String> chainChildren;

    public Card(String name, Color color, Requirements requirements, String chainParent, List<Effect> effects,
                List<String> chainChildren) {
        this.name = name;
        this.color = color;
        this.requirements = requirements;
        this.chainParent = chainParent;
        this.effects = effects;
        this.chainChildren = chainChildren;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public String getChainParent() {
        return chainParent;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public List<String> getChainChildren() {
        return chainChildren;
    }

    public boolean isChainableOn(Board board) {
        return board.isPlayed(chainParent);
    }

    public boolean isAffordedBy(Board board) {
        return requirements.isAffordedBy(board);
    }

    public boolean isPlayable(Board board, Board left, Board right) {
        return !board.isPlayed(name) && (isChainableOn(board) || requirements.isAffordedBy(board, left, right));
    }

    public void applyTo(Board board, Board left, Board right) {
        // TODO add paid resources cost deduction
        if (!isChainableOn(board)) {
            // TODO add paid resources exemption
            requirements.pay(board);
        }
        effects.forEach(e -> e.apply(board, left, right));
    }

    @Override
    public String toString() {
        return "Card{" + "name='" + name + '\'' + ", color=" + color + ", requirements=" + requirements + ", effects="
                + effects + '}';
    }
}

package org.luxons.sevenwonders.game.cards;

import java.util.List;
import java.util.Objects;

import org.luxons.sevenwonders.game.api.BoughtResources;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.effects.Effect;

public class Card {

    private final String name;

    private final Color color;

    private final Requirements requirements;

    private final List<Effect> effects;

    private final String chainParent;

    private final List<String> chainChildren;

    private final String image;

    private CardBack back;

    public Card(String name, Color color, Requirements requirements, List<Effect> effects, String chainParent,
            List<String> chainChildren, String image) {
        this.name = name;
        this.color = color;
        this.requirements = requirements;
        this.chainParent = chainParent;
        this.effects = effects;
        this.chainChildren = chainChildren;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public CardBack getBack() {
        return back;
    }

    public void setBack(CardBack back) {
        this.back = back;
    }

    public boolean isChainableOn(Board board) {
        return board.isPlayed(chainParent);
    }

    public boolean isAffordedBy(Board board) {
        return requirements.isAffordedBy(board);
    }

    public boolean isPlayable(Table table, int playerIndex) {
        Board board = table.getBoard(playerIndex);
        if (board.isPlayed(name)) {
            return false; // cannot play twice the same card
        }
        return isChainableOn(board) || requirements.isAffordedBy(table, playerIndex);
    }

    public void applyTo(Table table, int playerIndex, List<BoughtResources> boughtResources) {
        // TODO add paid resources cost deduction
        Board playerBoard = table.getBoard(playerIndex);
        if (!isChainableOn(playerBoard)) {
            // TODO add paid resources exemption
            requirements.pay(playerBoard);
        }
        effects.forEach(e -> e.apply(table, playerIndex));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Card card = (Card)o;
        return Objects.equals(name, card.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Card{" + "name='" + name + '\'' + ", color=" + color + ", requirements=" + requirements + ", effects="
                + effects + '}';
    }
}

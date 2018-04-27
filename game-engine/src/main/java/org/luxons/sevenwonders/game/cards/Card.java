package org.luxons.sevenwonders.game.cards;

import java.util.List;
import java.util.Objects;

import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.effects.Effect;
import org.luxons.sevenwonders.game.resources.ResourceTransactions;

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

    private boolean isAllowedOnBoard(Board board) {
        return !board.isPlayed(name); // cannot play twice the same card
    }

    public boolean isChainableOn(Board board) {
        return isAllowedOnBoard(board) && board.isPlayed(chainParent);
    }

    public boolean isFreeFor(Board board) {
        if (!isAllowedOnBoard(board)) {
            return false;
        }
        return isChainableOn(board) || (requirements.areMetWithoutNeighboursBy(board) && requirements.getGold() == 0);
    }

    public boolean isPlayable(Table table, int playerIndex) {
        Board board = table.getBoard(playerIndex);
        if (!isAllowedOnBoard(board)) {
            return false;
        }
        return isChainableOn(board) || requirements.areMetBy(table, playerIndex);
    }

    public void applyTo(Table table, int playerIndex, ResourceTransactions transactions) {
        Board playerBoard = table.getBoard(playerIndex);
        if (!isChainableOn(playerBoard)) {
            requirements.pay(table, playerIndex, transactions);
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
        Card card = (Card) o;
        return Objects.equals(name, card.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Card{" + name + '}';
    }
}

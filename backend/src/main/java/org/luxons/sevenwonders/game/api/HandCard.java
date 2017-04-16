package org.luxons.sevenwonders.game.api;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Card;

/**
 * A card with contextual information relative to the hand it is sitting in. The extra information is especially
 * useful because it frees the client from a painful business logic implementation.
 */
public class HandCard {

    private final Card card;

    private final boolean chainable;

    private final boolean free;

    private final boolean playable;

    public HandCard(Card card, Table table, int playerIndex) {
        Board board = table.getBoard(playerIndex);
        this.card = card;
        this.chainable = card.isChainableOn(board);
        this.free = card.isAffordedBy(board) && card.getRequirements().getGold() == 0;
        this.playable = card.isPlayable(table, playerIndex);
    }

    public Card getCard() {
        return card;
    }

    public boolean isChainable() {
        return chainable;
    }

    public boolean isFree() {
        return free;
    }

    public boolean isPlayable() {
        return playable;
    }

    @Override
    public String toString() {
        return "HandCard{" + "card=" + card + ", chainable=" + chainable + ", free=" + free + ", playable=" + playable
                + '}';
    }
}

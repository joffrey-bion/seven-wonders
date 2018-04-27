package org.luxons.sevenwonders.game.moves;

import java.util.List;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.resources.ResourceTransactions;

public abstract class Move {

    private int playerIndex;

    private Card card;

    private MoveType type;

    private ResourceTransactions transactions;

    Move(int playerIndex, Card card, PlayerMove move) {
        this.playerIndex = playerIndex;
        this.card = card;
        this.type = move.getType();
        this.transactions = new ResourceTransactions(move.getTransactions());
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public Card getCard() {
        return card;
    }

    public MoveType getType() {
        return type;
    }

    public ResourceTransactions getTransactions() {
        return transactions;
    }

    public abstract void validate(Table table, List<Card> playerHand) throws InvalidMoveException;

    public abstract void place(Table table, List<Card> discardedCards, Settings settings);

    public abstract void activate(Table table, List<Card> discardedCards, Settings settings);
}

package org.luxons.sevenwonders.game.api;

import java.util.ArrayList;
import java.util.Collection;

import org.luxons.sevenwonders.game.moves.MoveType;
import org.luxons.sevenwonders.game.resources.ResourceTransaction;

public class PlayerMove {

    private MoveType type;

    private String cardName;

    private Collection<ResourceTransaction> transactions = new ArrayList<>();

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Collection<ResourceTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Collection<ResourceTransaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        return type + " '" + cardName + '\'';
    }
}

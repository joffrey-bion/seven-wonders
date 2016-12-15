package org.luxons.sevenwonders.game.api;

import org.luxons.sevenwonders.game.cards.Card;

public class HandCard {

    private final Card data;

    private boolean chainable;

    private boolean free;

    private boolean playable;

    public HandCard(Card card) {
        this.data = card;
    }

    public Card getData() {
        return data;
    }

    public boolean isChainable() {
        return chainable;
    }

    public void setChainable(boolean chainable) {
        this.chainable = chainable;
    }

    public boolean isFree() {
        return free;
    }

    public void setFree(boolean free) {
        this.free = free;
    }

    public boolean isPlayable() {
        return playable;
    }

    public void setPlayable(boolean playable) {
        this.playable = playable;
    }
}

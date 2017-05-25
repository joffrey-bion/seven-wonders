package org.luxons.sevenwonders.test.api;

/**
 * A card with contextual information relative to the hand it is sitting in. The extra information is especially
 * useful because it frees the client from a painful business logic implementation.
 */
public class ApiHandCard {

    private ApiCard card;

    private boolean chainable;

    private boolean free;

    private boolean playable;

    public ApiCard getCard() {
        return card;
    }

    public void setCard(ApiCard card) {
        this.card = card;
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

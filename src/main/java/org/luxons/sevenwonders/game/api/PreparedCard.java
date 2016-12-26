package org.luxons.sevenwonders.game.api;

import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.cards.CardBack;

public class PreparedCard {

    private final Player player;

    private final CardBack cardBack;

    public PreparedCard(Player player, CardBack cardBack) {
        this.player = player;
        this.cardBack = cardBack;
    }

    public Player getPlayer() {
        return player;
    }

    public CardBack getCardBack() {
        return cardBack;
    }
}

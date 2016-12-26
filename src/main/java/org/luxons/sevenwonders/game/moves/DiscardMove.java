package org.luxons.sevenwonders.game.moves;

import java.util.List;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.Card;

public class DiscardMove extends Move {

    DiscardMove(int playerIndex, Card card, PlayerMove move) {
        super(playerIndex, card, move);
    }

    public boolean isValid(Table table) {
        return true;
    }

    @Override
    public void place(Table table, List<Card> discardedCards, Settings settings) {
        discardedCards.add(getCard());
    }

    @Override
    public void activate(Table table, List<Card> discardedCards, Settings settings) {
        table.giveGoldForDiscarded(getPlayerIndex(), settings.getDiscardedCardGold());
    }
}

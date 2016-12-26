package org.luxons.sevenwonders.game.moves;

import java.util.List;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.Card;

public class PlayCardMove extends Move {

    PlayCardMove(int playerIndex, Card card, PlayerMove move) {
        super(playerIndex, card, move);
    }

    public boolean isValid(Table table) {
        return getCard().getRequirements().isAffordedBy(table, getPlayerIndex(), getBoughtResources());
    }

    @Override
    public void place(Table table, List<Card> discardedCards, Settings settings) {
        table.placeCard(getPlayerIndex(), getCard());
    }

    @Override
    public void activate(Table table, List<Card> discardedCards, Settings settings) {
        table.activateCard(getPlayerIndex(), getCard(), getBoughtResources());
    }
}

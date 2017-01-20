package org.luxons.sevenwonders.game.moves;

import java.util.List;

import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.Card;

public abstract class CardFromHandMove extends Move {

    CardFromHandMove(int playerIndex, Card card, PlayerMove move) {
        super(playerIndex, card, move);
    }

    @Override
    public boolean isValid(Table table, List<Card> playerHand) {
        return playerHand.contains(getCard());
    }

}

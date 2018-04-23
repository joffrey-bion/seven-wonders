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
    public void validate(Table table, List<Card> playerHand) throws InvalidMoveException {
        if (!playerHand.contains(getCard())) {
            throw new InvalidMoveException(
                    String.format("Player %d does not have the card '%s' in his hand", getPlayerIndex(),
                            getCard().getName()));
        }
    }
}

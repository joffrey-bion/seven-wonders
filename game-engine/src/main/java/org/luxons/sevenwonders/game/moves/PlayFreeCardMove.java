package org.luxons.sevenwonders.game.moves;

import java.util.List;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Card;

public class PlayFreeCardMove extends CardFromHandMove {

    PlayFreeCardMove(int playerIndex, Card card, PlayerMove move) {
        super(playerIndex, card, move);
    }

    @Override
    public void validate(Table table, List<Card> playerHand) throws InvalidMoveException {
        super.validate(table, playerHand);
        Board board = table.getBoard(getPlayerIndex());
        if (!board.canPlayFreeCard(table.getCurrentAge())) {
            throw new InvalidMoveException(
                    String.format("Player %d cannot play the card %s for free", getPlayerIndex(), getCard().getName()));
        }
    }

    @Override
    public void place(Table table, List<Card> discardedCards, Settings settings) {
        Board board = table.getBoard(getPlayerIndex());
        board.addCard(getCard());
    }

    @Override
    public void activate(Table table, List<Card> discardedCards, Settings settings) {
        // only apply effects, without paying the cost
        getCard().getEffects().forEach(e -> e.apply(table, getPlayerIndex()));
        Board board = table.getBoard(getPlayerIndex());
        board.consumeFreeCard(table.getCurrentAge());
    }
}

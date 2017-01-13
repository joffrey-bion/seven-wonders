package org.luxons.sevenwonders.game.moves;

import java.util.List;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.effects.SpecialAbility;

public class CopyGuildMove extends Move {

    CopyGuildMove(int playerIndex, Card card, PlayerMove move) {
        super(playerIndex, card, move);
    }

    @Override
    public boolean isValid(Table table, List<Card> playerHand) {
        Board board = table.getBoard(getPlayerIndex());
        if (!board.hasSpecial(SpecialAbility.COPY_GUILD)) {
            return false;
        }
        if (getCard().getColor() != Color.PURPLE) {
            return false;
        }
        boolean leftNeighbourHasIt = neighbourHasTheCard(table, RelativeBoardPosition.LEFT);
        boolean rightNeighbourHasIt = neighbourHasTheCard(table, RelativeBoardPosition.RIGHT);
        return leftNeighbourHasIt || rightNeighbourHasIt;
    }

    private boolean neighbourHasTheCard(Table table, RelativeBoardPosition position) {
        Board neighbourBoard = table.getBoard(getPlayerIndex(), position);
        return neighbourBoard.getPlayedCards().contains(getCard());
    }

    @Override
    public void place(Table table, List<Card> discardedCards, Settings settings) {
        // nothing special to do here
    }

    @Override
    public void activate(Table table, List<Card> discardedCards, Settings settings) {
        Board board = table.getBoard(getPlayerIndex());
        board.setCopiedGuild(getCard());
    }
}

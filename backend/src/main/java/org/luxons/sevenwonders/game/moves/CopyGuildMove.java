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
    public void validate(Table table, List<Card> playerHand) throws InvalidMoveException {
        Board board = table.getBoard(getPlayerIndex());
        if (!board.hasSpecial(SpecialAbility.COPY_GUILD)) {
            throw new InvalidMoveException(
                    String.format("Player %d does not have the ability to copy guild cards", getPlayerIndex()));
        }
        if (getCard().getColor() != Color.PURPLE) {
            throw new InvalidMoveException(
                    String.format("Player %d cannot copy card %s because it is not a guild card", getPlayerIndex(),
                            getCard().getName()));
        }
        boolean leftNeighbourHasIt = neighbourHasTheCard(table, RelativeBoardPosition.LEFT);
        boolean rightNeighbourHasIt = neighbourHasTheCard(table, RelativeBoardPosition.RIGHT);
        if (!leftNeighbourHasIt && !rightNeighbourHasIt) {
            throw new InvalidMoveException(
                    String.format("Player %d cannot copy card %s because none of his neighbour has it",
                            getPlayerIndex(), getCard().getName()));
        }
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

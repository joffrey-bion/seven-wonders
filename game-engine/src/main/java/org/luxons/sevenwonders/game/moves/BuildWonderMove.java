package org.luxons.sevenwonders.game.moves;

import java.util.List;

import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Card;

public class BuildWonderMove extends CardFromHandMove {

    BuildWonderMove(int playerIndex, Card card, PlayerMove move) {
        super(playerIndex, card, move);
    }

    @Override
    public void validate(Table table, List<Card> playerHand) throws InvalidMoveException {
        super.validate(table, playerHand);
        Board board = table.getBoard(getPlayerIndex());
        if (!board.getWonder().isNextStageBuildable(table, getPlayerIndex(), getTransactions())) {
            throw new InvalidMoveException(
                    String.format("Player %d cannot upgrade his wonder with the given resources", getPlayerIndex()));
        }
    }

    @Override
    public void place(Table table, List<Card> discardedCards, Settings settings) {
        Board board = table.getBoard(getPlayerIndex());
        board.getWonder().buildLevel(getCard().getBack());
    }

    @Override
    public void activate(Table table, List<Card> discardedCards, Settings settings) {
        int playerIndex = getPlayerIndex();
        Board board = table.getBoard(playerIndex);
        board.getWonder().activateLastBuiltStage(table, playerIndex, getTransactions());
    }
}

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
    public boolean isValid(Table table, List<Card> playerHand) {
        if (!super.isValid(table, playerHand)) {
            return false;
        }
        Board board = table.getBoard(getPlayerIndex());
        return board.getWonder().isNextStageBuildable(table, getPlayerIndex(), getBoughtResources());
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
        board.getWonder().activateLastBuiltStage(table, playerIndex, getBoughtResources());
    }
}

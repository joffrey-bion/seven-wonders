package org.luxons.sevenwonders.game.api;

import java.util.List;

import org.luxons.sevenwonders.game.moves.Move;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.game.resources.BoughtResources;

/**
 * The table contains what is visible by all the players in the game: the boards and their played cards, and the
 * players' information.
 */
public class Table {

    private final int nbPlayers;

    private final List<Board> boards;

    private List<Move> lastPlayedMoves;

    public Table(List<Board> boards) {
        this.nbPlayers = boards.size();
        this.boards = boards;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public Board getBoard(int playerIndex) {
        return boards.get(playerIndex);
    }

    public Board getBoard(int playerIndex, RelativeBoardPosition position) {
        return boards.get(position.getIndexFrom(playerIndex, nbPlayers));
    }

    public List<Move> getLastPlayedMoves() {
        return lastPlayedMoves;
    }

    public void setLastPlayedMoves(List<Move> lastPlayedMoves) {
        this.lastPlayedMoves = lastPlayedMoves;
    }

    public void placeCard(int playerIndex, Card card) {
        Board board = boards.get(playerIndex);
        board.addCard(card);
    }

    public void buildWonderStage(int playerIndex, CardBack cardBack) {
        Board board = boards.get(playerIndex);
        board.getWonder().buildLevel(cardBack);
    }

    public void activateCard(int playerIndex, Card card, List<BoughtResources> boughtResources) {
        card.applyTo(this, playerIndex, boughtResources);
    }

    public void activateCurrentWonderStage(int playerIndex, List<BoughtResources> boughtResources) {
        Board board = boards.get(playerIndex);
        board.getWonder().activateLastBuiltStage(this, playerIndex, boughtResources);
    }

    public void giveGoldForDiscarded(int playerIndex, int goldBonus) {
        Board board = boards.get(playerIndex);
        board.addGold(goldBonus);
    }

    public void resolveMilitaryConflicts(int age) {
        for (int i = 0; i < nbPlayers; i++) {
            Board board1 = getBoard(i);
            Board board2 = getBoard((i + 1) % nbPlayers);
            resolveConflict(board1, board2, age);
        }
    }

    private static void resolveConflict(Board board1, Board board2, int age) {
        int shields1 = board1.getMilitary().getNbShields();
        int shields2 = board2.getMilitary().getNbShields();
        if (shields1 < shields2) {
            board2.getMilitary().victory(age);
            board1.getMilitary().defeat();
        } else if (shields1 > shields2) {
            board1.getMilitary().victory(age);
            board2.getMilitary().defeat();
        }
    }
}

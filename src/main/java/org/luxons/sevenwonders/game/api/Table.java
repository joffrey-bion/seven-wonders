package org.luxons.sevenwonders.game.api;

import java.util.List;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.cards.Card;

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

    public void upgradeWonderStage(int playerIndex) {
        Board board = boards.get(playerIndex);
        board.increaseWonderLevel();
    }

    public void activateCard(int playerIndex, Card card) {
        card.applyTo(this, playerIndex);
    }

    public void activateCurrentWonderStage(int playerIndex) {
        Board board = boards.get(playerIndex);
        board.activateCurrentWonderLevel(this, playerIndex);
    }

    public void discard(int playerIndex, int goldBonus) {
        Board board = boards.get(playerIndex);
        board.setGold(board.getGold() + goldBonus);
    }
}

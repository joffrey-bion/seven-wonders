package org.luxons.sevenwonders.game.api;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Color;
import org.luxons.sevenwonders.game.cards.HandRotationDirection;
import org.luxons.sevenwonders.game.moves.Move;
import org.luxons.sevenwonders.game.resources.Provider;

/**
 * The table contains what is visible by all the players in the game: the boards and their played cards, and the
 * players' information.
 */
public class Table {

    private final int nbPlayers;

    private final List<Board> boards;

    private int currentAge = 0;

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

    public int getCurrentAge() {
        return currentAge;
    }

    public void increaseCurrentAge() {
        this.currentAge++;
    }

    public HandRotationDirection getHandRotationDirection() {
        return HandRotationDirection.forAge(currentAge);
    }

    public void resolveMilitaryConflicts() {
        for (int i = 0; i < nbPlayers; i++) {
            Board board1 = getBoard(i);
            Board board2 = getBoard((i + 1) % nbPlayers);
            resolveConflict(board1, board2, currentAge);
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

    public List<Card> getNeighbourGuildCards(int playerIndex) {
        return getNeighbourBoards(playerIndex).stream()
                                              .map(Board::getPlayedCards)
                                              .flatMap(List::stream)
                                              .filter(c -> c.getColor() == Color.PURPLE)
                                              .collect(Collectors.toList());
    }

    private List<Board> getNeighbourBoards(int playerIndex) {
        Provider[] providers = Provider.values();
        List<Board> boards = new ArrayList<>(providers.length);
        for (Provider provider : providers) {
            boards.add(getBoard(playerIndex, provider.getBoardPosition()));
        }
        return boards;
    }
}

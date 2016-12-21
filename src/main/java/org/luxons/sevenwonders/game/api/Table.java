package org.luxons.sevenwonders.game.api;

import java.util.List;
import java.util.stream.Collectors;

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

    public Table(List<Board> boards) {
        this.nbPlayers = boards.size();
        this.boards = boards;
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

    public int getNbPlayers() {
        return nbPlayers;
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

    public PlayerTurnInfo createPlayerTurnInfo(int playerIndex, List<Card> cards) {
        PlayerTurnInfo pti = new PlayerTurnInfo(playerIndex, this);
        pti.setHand(createHand(playerIndex, cards));
        return pti;
    }

    private List<HandCard> createHand(int playerIndex, List<Card> cards) {
        return cards.stream().map(c -> new HandCard(c, this, playerIndex)).collect(Collectors.toList());
    }
}

package org.luxons.sevenwonders.game.api;

import java.util.List;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Card;

/**
 * The table contains what is visible by all the players in the game: the boards and their played cards, and the
 * players' information.
 */
public class Table {

    private final int nbPlayers;

    private final List<Player> players;

    private final List<Board> boards;

    public Table(List<Player> players, List<Board> boards) {
        this.nbPlayers = players.size();
        this.players = players;
        this.boards = boards;
        if (players.size() != boards.size()) {
            throw new IllegalArgumentException(
                    String.format("There are %d boards for %d players, it doesn't make sense", boards.size(),
                            players.size()));
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Board> getBoards() {
        return boards;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public void placeCard(int playerIndex, Card card) {
        Board board = boards.get(playerIndex);
        board.addCard(card);
    }

    public void upgradeWonder(int playerIndex) {
        Board board = boards.get(playerIndex);
        Board left = boards.get(wrapIndex(playerIndex - 1));
        Board right = boards.get(wrapIndex(playerIndex + 1));
        board.upgradeWonderLevel(left, right);
    }

    public void playCard(int playerIndex, Card card) {
        Board board = boards.get(playerIndex);
        Board left = boards.get(wrapIndex(playerIndex - 1));
        Board right = boards.get(wrapIndex(playerIndex + 1));
        card.applyTo(board, left, right);
    }

    public void discard(int playerIndex, int goldBonus) {
        Board board = boards.get(playerIndex);
        board.setGold(board.getGold() + goldBonus);
    }

    private int wrapIndex(int index) {
        return Math.floorMod(index, nbPlayers);
    }

    public PlayerTurnInfo createPlayerTurnInfo(int playerIndex, List<Card> cards) {
        PlayerTurnInfo pti = new PlayerTurnInfo(playerIndex, this);
        pti.setHand(createHand(playerIndex, cards));
        return pti;
    }

    private List<HandCard> createHand(int playerIndex, List<Card> cards) {
        return cards.stream().map(c -> createHandCard(playerIndex, c)).collect(Collectors.toList());
    }

    private HandCard createHandCard(int playerIndex, Card card) {
        Board board = boards.get(playerIndex);
        Board left = boards.get(wrapIndex(playerIndex - 1));
        Board right = boards.get(wrapIndex(playerIndex + 1));
        HandCard handCard = new HandCard(card);
        handCard.setChainable(card.isChainableOn(board));
        handCard.setFree(card.isAffordedBy(board) && card.getRequirements().getGold() == 0);
        handCard.setPlayable(card.isPlayable(board, left, right));
        return handCard;
    }
}

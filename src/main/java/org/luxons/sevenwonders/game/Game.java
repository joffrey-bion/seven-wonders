package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Card;

public class Game {

    private final long id;

    private final Settings settings;

    private final Table table;

    private final Decks decks;

    private final List<Card> discardedCards;

    private final Map<Integer, Move> preparedMoves;

    private Map<Integer, List<Card>> hands;

    private int currentAge = 0;

    public Game(long id, Settings settings, List<Player> players, List<Board> boards, Decks decks) {
        this.id = id;
        this.settings = settings;
        this.table = new Table(players, boards);
        this.decks = decks;
        this.discardedCards = new ArrayList<>();
        this.hands = new HashMap<>();
        this.preparedMoves = new HashMap<>();
    }

    public long getId() {
        return id;
    }

    public int startNewAge() {
        currentAge++;
        hands = decks.deal(currentAge, table.getNbPlayers());
        return currentAge;
    }

    public List<PlayerTurnInfo> startTurn() {
        return hands.entrySet()
                    .stream()
                    .map(e -> table.createPlayerTurnInfo(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
    }

    public void prepareCard(int playerIndex, Move move) throws InvalidMoveException {
        validateMove(playerIndex, move);
        preparedMoves.put(playerIndex, move);
    }

    private void validateMove(int playerIndex, Move move) throws InvalidMoveException {
        // TODO throw if invalid
    }

    public boolean areAllPlayersReady() {
        return preparedMoves.size() == table.getPlayers().size();
    }

    public void playTurn() {
        // cards need to be all placed first as some effects depend on just-played cards
        placePreparedCards();
        playPreparedCards();
    }

    private void placePreparedCards() {
        preparedMoves.forEach((playerIndex, move) -> {
            switch (move.getType()) {
                case PLAY:
                    table.placeCard(playerIndex, decks.getCard(move.getCardName()));
                    break;
                case UPGRADE_WONDER:
                    // TODO pre-upgrade the level of wonder without effect
                    break;
                case DISCARD:
                    break;
            }
        });

    }

    private void playPreparedCards() {
        preparedMoves.forEach((playerIndex, move) -> {
            switch (move.getType()) {
                case PLAY:
                    table.playCard(playerIndex, decks.getCard(move.getCardName()));
                    break;
                case UPGRADE_WONDER:
                    table.upgradeWonder(playerIndex);
                    break;
                case DISCARD:
                    table.discard(playerIndex, settings.getDiscardedCardGold());
                    break;
            }
        });

    }

    public class InvalidMoveException extends RuntimeException {
    }
}

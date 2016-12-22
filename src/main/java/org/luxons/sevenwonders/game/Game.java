package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.api.HandCard;
import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.Decks;

public class Game {

    private final long id;

    private final Settings settings;

    private final List<Player> players;

    private final Table table;

    private final Decks decks;

    private final List<Card> discardedCards;

    private final Map<Integer, Move> preparedMoves;

    private Map<Integer, List<Card>> hands;

    private int currentAge = 0;

    public Game(long id, Settings settings, List<Player> players, List<Board> boards, Decks decks) {
        this.id = id;
        this.settings = settings;
        this.players = players;
        this.table = new Table(boards);
        this.decks = decks;
        this.discardedCards = new ArrayList<>();
        this.hands = new HashMap<>();
        this.preparedMoves = new HashMap<>();
        startNewAge();
    }

    public long getId() {
        return id;
    }

    public boolean containsUser(String userName) {
        return players.stream().anyMatch(p -> p.getUserName().equals(userName));
    }

    private void startNewAge() {
        currentAge++;
        hands = decks.deal(currentAge, table.getNbPlayers());
    }

    public List<PlayerTurnInfo> getTurnInfo() {
        return hands.entrySet().stream().map(e -> createPlayerTurnInfo(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
    }

    private PlayerTurnInfo createPlayerTurnInfo(int playerIndex, List<Card> cards) {
        PlayerTurnInfo pti = new PlayerTurnInfo(playerIndex, table);
        pti.setHand(createHand(playerIndex, cards));
        pti.setCurrentAge(currentAge);
        return pti;
    }

    private List<HandCard> createHand(int playerIndex, List<Card> cards) {
        return cards.stream().map(c -> new HandCard(c, table, playerIndex)).collect(Collectors.toList());
    }

    public void prepareCard(Move move) throws InvalidMoveException {
        validate(move);
        preparedMoves.put(move.getPlayerIndex(), move);
    }

    private void validate(Move move) throws InvalidMoveException {
        List<Card> hand = hands.get(move.getPlayerIndex());
        if (hand == null) {
            throw new InvalidMoveException("Invalid player index " + move.getPlayerIndex());
        }
        Card card = decks.getCard(move.getCardName());
        if (!hand.contains(card)) {
            throw new InvalidMoveException(
                    "Player " + move.getPlayerIndex() + " does not have the card " + move.getCardName());
        }
        if (!move.isValid(table)) {
            throw new InvalidMoveException(
                    "Player " + move.getPlayerIndex() + " cannot play the card " + move.getCardName());
        }
    }

    public boolean areAllPlayersReady() {
        return preparedMoves.size() == players.size();
    }

    public void playTurn() {
        List<Move> playedMoves = mapToList(preparedMoves);

        // cards need to be all placed first as some effects depend on just-played cards
        placePreparedCards(playedMoves);
        activatePlayedCards(playedMoves);
        table.setLastPlayedMoves(playedMoves);
        preparedMoves.clear();
        if (endOfAgeReached()) {
            startNewAge();
        } else {
            rotateHands();
        }
    }

    private static List<Move> mapToList(Map<Integer, Move> movesPerPlayer) {
        List<Move> moves = new ArrayList<>(movesPerPlayer.size());
        for (int p = 0; p < movesPerPlayer.size(); p++) {
            Move move = movesPerPlayer.get(p);
            if (move == null) {
                throw new MissingPreparedMoveException(p);
            }
            moves.add(move);
        }
        return moves;
    }

    private void placePreparedCards(List<Move> playedMoves) {
        playedMoves.forEach(move -> {
            placeCard(move);
            Card card = decks.getCard(move.getCardName());
            hands.get(move.getPlayerIndex()).remove(card);
        });
    }

    private void placeCard(Move move) {
        switch (move.getType()) {
        case PLAY:
            table.placeCard(move.getPlayerIndex(), decks.getCard(move.getCardName()));
            break;
        case UPGRADE_WONDER:
            table.upgradeWonderStage(move.getPlayerIndex());
            break;
        case DISCARD:
            discardedCards.add(decks.getCard(move.getCardName()));
            break;
        }
    }

    private void activatePlayedCards(List<Move> playedMoves) {
        playedMoves.forEach(this::activateCard);
    }

    private void activateCard(Move move) {
        switch (move.getType()) {
        case PLAY:
            table.activateCard(move.getPlayerIndex(), decks.getCard(move.getCardName()));
            break;
        case UPGRADE_WONDER:
            table.activateCurrentWonderStage(move.getPlayerIndex());
            break;
        case DISCARD:
            table.discard(move.getPlayerIndex(), settings.getDiscardedCardGold());
            break;
        }
    }

    private boolean endOfAgeReached() {
        return hands.get(0).size() == 1;
    }

    private void rotateHands() {
        int offset = getHandRotationOffset();
        Map<Integer, List<Card>> newHands = new HashMap<>(hands.size());
        for (int i = 0; i < players.size(); i++) {
            int newIndex = Math.floorMod(i + offset, players.size());
            newHands.put(newIndex, hands.get(i));
        }
        hands = newHands;
    }

    private int getHandRotationOffset() {
        // clockwise at age 1, and alternating
        return currentAge % 2 == 0 ? -1 : 1;
    }

    private static class MissingPreparedMoveException extends RuntimeException {
        MissingPreparedMoveException(int playerIndex) {
            super("Player " + playerIndex + " is not ready to play");
        }
    }

    private static class InvalidMoveException extends RuntimeException {
        InvalidMoveException(String message) {
            super(message);
        }
    }
}

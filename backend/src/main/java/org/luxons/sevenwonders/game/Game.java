package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.luxons.sevenwonders.game.api.Action;
import org.luxons.sevenwonders.game.api.HandCard;
import org.luxons.sevenwonders.game.api.PlayerMove;
import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.cards.Card;
import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.game.cards.Decks;
import org.luxons.sevenwonders.game.cards.Hands;
import org.luxons.sevenwonders.game.effects.SpecialAbility;
import org.luxons.sevenwonders.game.moves.InvalidMoveException;
import org.luxons.sevenwonders.game.moves.Move;
import org.luxons.sevenwonders.game.scoring.ScoreBoard;

public class Game {

    private static final int LAST_AGE = 3;

    private final long id;

    private final Settings settings;

    private final int nbPlayers;

    private final Table table;

    private final Decks decks;

    private final List<Card> discardedCards;

    private final Map<Integer, Move> preparedMoves;

    private Map<Integer, PlayerTurnInfo> currentTurnInfo;

    private Hands hands;

    public Game(long id, Settings settings, int nbPlayers, List<Board> boards, Decks decks) {
        this.id = id;
        this.settings = settings;
        this.nbPlayers = nbPlayers;
        this.table = new Table(boards);
        this.decks = decks;
        this.discardedCards = new ArrayList<>();
        this.currentTurnInfo = new HashMap<>();
        this.preparedMoves = new HashMap<>();
        startNewAge();
    }

    public long getId() {
        return id;
    }

    public Settings getSettings() {
        return settings;
    }

    private void startNewAge() {
        table.increaseCurrentAge();
        hands = decks.deal(table.getCurrentAge(), table.getNbPlayers());
        startNewTurn();
    }

    private void startNewTurn() {
        currentTurnInfo.clear();
        for (int i = 0; i < nbPlayers; i++) {
            currentTurnInfo.put(i, createPlayerTurnInfo(i));
        }
    }

    private PlayerTurnInfo createPlayerTurnInfo(int playerIndex) {
        PlayerTurnInfo pti = new PlayerTurnInfo(playerIndex, table);
        List<HandCard> hand = hands.createHand(table, playerIndex);
        pti.setHand(hand);
        Action action = determineAction(hand, table.getBoard(playerIndex));
        pti.setAction(action);
        pti.setMessage(action.getMessage());
        if (action == Action.PICK_NEIGHBOR_GUILD) {
            pti.setNeighbourGuildCards(table.getNeighbourGuildCards(playerIndex));
        }
        return pti;
    }

    public Collection<PlayerTurnInfo> getCurrentTurnInfo() {
        return currentTurnInfo.values();
    }

    private Action determineAction(List<HandCard> hand, Board board) {
        if (endOfGameReached() && board.hasSpecial(SpecialAbility.COPY_GUILD)) {
            return Action.PICK_NEIGHBOR_GUILD;
        } else if (hand.size() == 1 && board.hasSpecial(SpecialAbility.PLAY_LAST_CARD)) {
            return Action.PLAY_LAST;
        } else if (hand.size() == 2 && board.hasSpecial(SpecialAbility.PLAY_LAST_CARD)) {
            return Action.PLAY_2;
        } else if (hand.isEmpty()) {
            return Action.WAIT;
        } else {
            return Action.PLAY;
        }
    }

    public CardBack prepareMove(int playerIndex, PlayerMove playerMove) throws InvalidMoveException {
        Card card = decks.getCard(playerMove.getCardName());
        Move move = playerMove.getType().resolve(playerIndex, card, playerMove);
        validate(move);
        preparedMoves.put(playerIndex, move);
        return card.getBack();
    }

    private void validate(Move move) throws InvalidMoveException {
        List<Card> hand = hands.get(move.getPlayerIndex());
        move.validate(table, hand);
    }

    public boolean areAllPlayersReady() {
        long nbExpectedMoves = currentTurnInfo.values().stream().filter(pti -> pti.getAction() != Action.WAIT).count();
        return preparedMoves.size() == nbExpectedMoves;
    }

    public void playTurn() {
        makeMoves();
        if (endOfAgeReached()) {
            executeEndOfAgeEvents();
            if (!endOfGameReached()) {
                startNewAge();
            }
        } else {
            rotateHandsIfRelevant();
            startNewTurn();
        }
    }

    private void rotateHandsIfRelevant() {
        // we don't rotate hands if some player can play his last card (with the special ability)
        if (!hands.maxOneCardRemains()) {
            hands.rotate(table.getHandRotationDirection());
        }
    }

    private void makeMoves() {
        List<Move> playedMoves = mapToList(preparedMoves);

        // all cards from this turn need to be placed before executing any effect
        // because effects depending on played cards need to take the ones from the current turn into account too
        placePreparedCards(playedMoves);

        // same goes for the discarded cards during the last turn, which should be available for special actions
        if (hands.maxOneCardRemains()) {
            discardLastCardsOfHands();
        }

        activatePlayedCards(playedMoves);

        table.setLastPlayedMoves(playedMoves);
        preparedMoves.clear();
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
            move.place(table, discardedCards, settings);
            removeFromHand(move.getPlayerIndex(), move.getCard());
        });
    }

    private void discardLastCardsOfHands() {
        for (int i = 0; i < nbPlayers; i++) {
            Board board = table.getBoard(i);
            if (!board.hasSpecial(SpecialAbility.PLAY_LAST_CARD)) {
                discardHand(i);
            }
        }
    }

    private void discardHand(int playerIndex) {
        List<Card> hand = hands.get(playerIndex);
        discardedCards.addAll(hand);
        hand.clear();
    }

    private void removeFromHand(int playerIndex, Card card) {
        hands.get(playerIndex).remove(card);
    }

    private void activatePlayedCards(List<Move> playedMoves) {
        playedMoves.forEach(move -> move.activate(table, discardedCards, settings));
    }

    private boolean endOfAgeReached() {
        return hands.isEmpty();
    }

    private void executeEndOfAgeEvents() {
        table.resolveMilitaryConflicts();
    }

    private boolean endOfGameReached() {
        return endOfAgeReached() && table.getCurrentAge() == LAST_AGE;
    }

    public ScoreBoard computeScore() {
        ScoreBoard scoreBoard = new ScoreBoard();
        table.getBoards().stream().map(b -> b.computePoints(table)).forEach(scoreBoard::add);
        return scoreBoard;
    }

    private static class MissingPreparedMoveException extends IllegalStateException {
        MissingPreparedMoveException(int playerIndex) {
            super("Player " + playerIndex + " is not ready to play");
        }
    }
}

package org.luxons.sevenwonders.test.api;

import java.util.List;

import org.luxons.sevenwonders.game.cards.HandRotationDirection;
import org.luxons.sevenwonders.game.moves.Move;

public class ApiTable {

    private int nbPlayers;

    private List<ApiBoard> boards;

    private int currentAge = 0;

    private HandRotationDirection handRotationDirection;

    private List<Move> lastPlayedMoves;

    private List<ApiCard> neighbourGuildCards;

    public int getNbPlayers() {
        return nbPlayers;
    }

    public void setNbPlayers(int nbPlayers) {
        this.nbPlayers = nbPlayers;
    }

    public List<ApiBoard> getBoards() {
        return boards;
    }

    public void setBoards(List<ApiBoard> boards) {
        this.boards = boards;
    }

    public int getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }

    public HandRotationDirection getHandRotationDirection() {
        return handRotationDirection;
    }

    public void setHandRotationDirection(HandRotationDirection handRotationDirection) {
        this.handRotationDirection = handRotationDirection;
    }

    public List<Move> getLastPlayedMoves() {
        return lastPlayedMoves;
    }

    public void setLastPlayedMoves(List<Move> lastPlayedMoves) {
        this.lastPlayedMoves = lastPlayedMoves;
    }

    public void increaseCurrentAge() {
        this.currentAge++;
    }

    public List<ApiCard> getNeighbourGuildCards() {
        return neighbourGuildCards;
    }

    public void setNeighbourGuildCards(List<ApiCard> neighbourGuildCards) {
        this.neighbourGuildCards = neighbourGuildCards;
    }
}

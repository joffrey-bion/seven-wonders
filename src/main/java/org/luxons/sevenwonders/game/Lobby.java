package org.luxons.sevenwonders.game;

import org.luxons.sevenwonders.game.data.GameDefinition;

import java.util.ArrayList;
import java.util.List;

public class Lobby {

    private final long id;

    private final List<Player> players;

    private final GameDefinition gameDefinition;

    private State state = State.LOBBY;

    public Lobby(long id, GameDefinition gameDefintion) {
        this.id = id;
        this.gameDefinition = gameDefintion;
        this.players = new ArrayList<>(3);
    }

    public long getId() {
        return id;
    }

    public synchronized int addPlayer(Player player) {
        if (hasStarted()) {
            throw new GameAlreadyStartedException();
        }
        if (maxPlayersReached()) {
            throw new PlayerOverflowException();
        }
        int playerId = players.size();
        players.add(player);
        return playerId;
    }

    private boolean hasStarted() {
        return state != State.LOBBY;
    }

    public synchronized Game startGame(Settings settings) {
        if (!hasEnoughPlayers()) {
            throw new PlayerUnderflowException();
        }
        state = State.PLAYING;
        settings.setNbPlayers(players.size());
        return gameDefinition.initGame(settings);
    }

    private boolean maxPlayersReached() {
        return players.size() >= gameDefinition.getMaxPlayers();
    }

    private boolean hasEnoughPlayers() {
        return players.size() >= gameDefinition.getMinPlayers();
    }

    public class GameAlreadyStartedException extends IllegalStateException {
    }

    public class PlayerOverflowException extends IllegalStateException {
    }

    public class PlayerUnderflowException extends IllegalStateException {
    }
}

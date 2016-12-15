package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.data.GameDefinition;

public class Lobby {

    private final long id;

    private final List<Player> players;

    private final GameDefinition gameDefinition;

    private State state = State.LOBBY;

    public Lobby(long id, GameDefinition gameDefinition) {
        this.id = id;
        this.gameDefinition = gameDefinition;
        this.players = new ArrayList<>(3);
    }

    public long getId() {
        return id;
    }

    public synchronized int addPlayer(Player player) throws GameAlreadyStartedException, PlayerOverflowException {
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

    private boolean maxPlayersReached() {
        return players.size() >= gameDefinition.getMaxPlayers();
    }

    public synchronized Game startGame(Settings settings) throws PlayerUnderflowException {
        if (!hasEnoughPlayers()) {
            throw new PlayerUnderflowException();
        }
        state = State.PLAYING;
        settings.setNbPlayers(players.size());
        return gameDefinition.initGame(id, settings, players);
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

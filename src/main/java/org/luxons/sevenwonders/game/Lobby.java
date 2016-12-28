package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.data.GameDefinition;

public class Lobby {

    private final long id;

    private final String name;

    private final Player owner;

    private final GameDefinition gameDefinition;

    private List<Player> players;

    private State state = State.LOBBY;

    public Lobby(long id, String name, Player owner, GameDefinition gameDefinition) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.gameDefinition = gameDefinition;
        this.players = new ArrayList<>(gameDefinition.getMinPlayers());
        players.add(owner);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public synchronized int addPlayer(Player player) throws GameAlreadyStartedException, PlayerOverflowException {
        if (hasStarted()) {
            throw new GameAlreadyStartedException();
        }
        if (maxPlayersReached()) {
            throw new PlayerOverflowException();
        }
        if (playerNameAlreadyUsed(player.getDisplayName())) {
            throw new PlayerNameAlreadyUsedException(player.getDisplayName());
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

    private boolean playerNameAlreadyUsed(String name) {
        return players.stream().anyMatch(p -> p.getDisplayName().equals(name));
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

    public void reorderPlayers(List<String> orderedUserNames) {
        players = orderedUserNames.stream().map(this::getPlayer).collect(Collectors.toList());
    }

    private Player getPlayer(String userName) {
        return players.stream()
                      .filter(p -> p.getUserName().equals(userName))
                      .findAny()
                      .orElseThrow(() -> new UnknownPlayerException(userName));
    }

    public boolean isOwner(String userName) {
        return owner.getUserName().equals(userName);
    }

    public boolean containsUser(String userName) {
        return players.stream().anyMatch(p -> p.getUserName().equals(userName));
    }

    static class GameAlreadyStartedException extends IllegalStateException {
    }

    static class PlayerOverflowException extends IllegalStateException {
    }

    static class PlayerUnderflowException extends IllegalStateException {
    }

    static class PlayerNameAlreadyUsedException extends RuntimeException {
        PlayerNameAlreadyUsedException(String name) {
            super(name);
        }
    }

    static class UnknownPlayerException extends IllegalArgumentException {
        UnknownPlayerException(String userName) {
            super(userName);
        }
    }
}

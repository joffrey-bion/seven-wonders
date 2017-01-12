package org.luxons.sevenwonders.game;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.data.GameDefinition;

public class Lobby {

    private final long id;

    private final String name;

    private final Player owner;

    private final GameDefinition gameDefinition;

    private final List<Player> players;

    private CustomizableSettings settings;

    private State state = State.LOBBY;

    public Lobby(long id, String name, Player owner, GameDefinition gameDefinition) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.gameDefinition = gameDefinition;
        this.players = new ArrayList<>(gameDefinition.getMinPlayers());
        this.settings = new CustomizableSettings();
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

    public CustomizableSettings getSettings() {
        return settings;
    }

    public void setSettings(CustomizableSettings settings) {
        this.settings = settings;
    }

    public synchronized void addPlayer(Player player) throws GameAlreadyStartedException, PlayerOverflowException {
        if (hasStarted()) {
            throw new GameAlreadyStartedException();
        }
        if (maxPlayersReached()) {
            throw new PlayerOverflowException();
        }
        if (playerNameAlreadyUsed(player.getDisplayName())) {
            throw new PlayerNameAlreadyUsedException(player.getDisplayName());
        }
        player.setIndex(players.size());
        players.add(player);
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

    public synchronized Game startGame() throws PlayerUnderflowException {
        if (!hasEnoughPlayers()) {
            throw new PlayerUnderflowException();
        }
        state = State.PLAYING;
        return gameDefinition.initGame(id, settings, players);
    }

    private boolean hasEnoughPlayers() {
        return players.size() >= gameDefinition.getMinPlayers();
    }

    public void reorderPlayers(List<String> orderedUserNames) {
        List<Player> formerList = new ArrayList<>(players);
        players.clear();
        for (int i = 0; i < orderedUserNames.size(); i++) {
            Player player = getPlayer(formerList, orderedUserNames.get(i));
            players.add(player);
            player.setIndex(i);
        }
    }

    private static Player getPlayer(List<Player> players, String userName) {
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

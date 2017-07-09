package org.luxons.sevenwonders.lobby;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.data.GameDefinition;

public class Lobby {

    private final long id;

    private final String name;

    private final Player owner;

    private final transient GameDefinition gameDefinition;

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
        addPlayer(owner);
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

    public State getState() {
        return state;
    }

    public synchronized void addPlayer(Player player) throws GameAlreadyStartedException, PlayerOverflowException {
        if (hasStarted()) {
            throw new GameAlreadyStartedException(name);
        }
        if (maxPlayersReached()) {
            throw new PlayerOverflowException(gameDefinition.getMaxPlayers());
        }
        if (playerNameAlreadyUsed(player.getDisplayName())) {
            throw new PlayerNameAlreadyUsedException(player.getDisplayName(), name);
        }
        player.setIndex(players.size());
        player.setLobby(this);
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
            throw new PlayerUnderflowException(gameDefinition.getMinPlayers());
        }
        state = State.PLAYING;
        Game game = gameDefinition.initGame(id, settings, players.size());
        players.forEach(p -> p.setGame(game));
        return game;
    }

    private boolean hasEnoughPlayers() {
        return players.size() >= gameDefinition.getMinPlayers();
    }

    public void reorderPlayers(List<String> orderedUsernames) throws UnknownPlayerException {
        List<Player> formerList = new ArrayList<>(players);
        players.clear();
        for (int i = 0; i < orderedUsernames.size(); i++) {
            Player player = getPlayer(formerList, orderedUsernames.get(i));
            players.add(player);
            player.setIndex(i);
        }
    }

    private static Player getPlayer(List<Player> players, String username) throws UnknownPlayerException {
        return players.stream()
                      .filter(p -> p.getUsername().equals(username))
                      .findAny()
                      .orElseThrow(() -> new UnknownPlayerException(username));
    }

    public boolean isOwner(String username) {
        return owner.getUsername().equals(username);
    }

    public boolean containsUser(String username) {
        return players.stream().anyMatch(p -> p.getUsername().equals(username));
    }

    public Player removePlayer(String username) throws UnknownPlayerException {
        Player player = getPlayer(players, username);
        players.remove(player);
        player.setIndex(-1);
        player.setLobby(null);
        player.setGame(null);
        return player;
    }

    static class GameAlreadyStartedException extends IllegalStateException {
        GameAlreadyStartedException(String name) {
            super(String.format("Game '%s' has already started", name));
        }
    }

    static class PlayerOverflowException extends IllegalStateException {
        PlayerOverflowException(int max) {
            super(String.format("Maximum %d players allowed", max));
        }
    }

    static class PlayerUnderflowException extends IllegalStateException {
        PlayerUnderflowException(int min) {
            super(String.format("Minimum %d players required to start a game", min));
        }
    }

    static class PlayerNameAlreadyUsedException extends IllegalStateException {
        PlayerNameAlreadyUsedException(String displayName, String gameName) {
            super(String.format("Name '%s' is already used by a player in game '%s'", displayName, gameName));
        }
    }

    static class UnknownPlayerException extends IllegalStateException {
        UnknownPlayerException(String username) {
            super(String.format("Unknown player '%s'", username));
        }
    }
}

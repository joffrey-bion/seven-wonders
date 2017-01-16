package org.luxons.sevenwonders.repositories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LobbyRepository {

    private final GameDefinitionLoader gameDefinitionLoader;

    private Map<Long, Lobby> lobbies = new HashMap<>();

    private long lastGameId = 0;

    @Autowired
    public LobbyRepository(GameDefinitionLoader gameDefinitionLoader) {
        this.gameDefinitionLoader = gameDefinitionLoader;
    }

    public Collection<Lobby> list() {
        return lobbies.values();
    }

    public Lobby create(String gameName, Player owner) {
        long id = lastGameId++;
        Lobby lobby = new Lobby(id, gameName, owner, gameDefinitionLoader.getGameDefinition());
        lobbies.put(id, lobby);
        return lobby;
    }

    public Lobby find(long lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null) {
            throw new LobbyNotFoundException(String.valueOf(lobbyId));
        }
        return lobby;
    }

    public static class LobbyNotFoundException extends RuntimeException {
        LobbyNotFoundException(String name) {
            super("Lobby not found for game '" + name + "'");
        }
    }
}

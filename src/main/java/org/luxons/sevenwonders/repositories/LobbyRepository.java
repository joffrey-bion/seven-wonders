package org.luxons.sevenwonders.repositories;

import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.errors.UserInputException;
import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class LobbyRepository {

    private final GameDefinitionLoader gameDefinitionLoader;

    private Map<String, Lobby> lobbies = new HashMap<>();

    private long lastGameId = 0;

    @Autowired
    public LobbyRepository(GameDefinitionLoader gameDefinitionLoader) {
        this.gameDefinitionLoader = gameDefinitionLoader;
    }

    public Lobby create(String gameName, Player owner) {
        if (lobbies.containsKey(gameName)) {
            throw new GameNameAlreadyUsedException(gameName);
        }
        long id = lastGameId++;
        Lobby lobby = new Lobby(id, gameName, owner, gameDefinitionLoader.getGameDefinition());
        lobbies.put(gameName, lobby);
        return lobby;
    }

    public Lobby find(String gameName) {
        Lobby lobby = lobbies.get(gameName);
        if (lobby == null) {
            throw new LobbyNotFoundException(gameName);
        }
        return lobby;
    }

    private static class LobbyNotFoundException extends RuntimeException {
        LobbyNotFoundException(String name) {
            super("Lobby not found for game '" + name + "'");
        }
    }

    private static class GameNameAlreadyUsedException extends UserInputException {
        GameNameAlreadyUsedException(String name) {
            super("Game name '" + name + "' already exists");
        }
    }
}

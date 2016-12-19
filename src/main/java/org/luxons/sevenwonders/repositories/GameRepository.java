package org.luxons.sevenwonders.repositories;

import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.errors.ApiMisuseException;
import org.luxons.sevenwonders.game.Game;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepository {

    private Map<Long, Game> games = new HashMap<>();

    public void add(Game game) {
        if (games.containsKey(game.getId())) {
            throw new GameAlreadyExistsException(game.getId());
        }
        games.put(game.getId(), game);
    }

    public Game find(long gameId) {
        Game game = games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException(gameId);
        }
        return game;
    }

    private static class GameNotFoundException extends ApiMisuseException {
        GameNotFoundException(long id) {
            super("Game " + id + " doesn't exist");
        }
    }

    private static class GameAlreadyExistsException extends ApiMisuseException {
        GameAlreadyExistsException(long id) {
            super("Game " + id + " already exists");
        }
    }
}

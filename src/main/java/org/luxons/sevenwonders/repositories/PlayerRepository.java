package org.luxons.sevenwonders.repositories;

import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.errors.ApiMisuseException;
import org.luxons.sevenwonders.game.Player;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRepository {

    private Map<String, Player> players = new HashMap<>();

    public Player updateOrCreatePlayer(String username, String displayName) {
        if (players.containsKey(username)) {
            return updatePlayerName(username, displayName);
        } else {
            return createPlayer(username, displayName);
        }
    }

    private Player createPlayer(String username, String displayName) throws PlayerAlreadyExistsException {
        Player player = new Player(username, displayName);
        add(player);
        return player;
    }

    private void add(Player player) throws PlayerAlreadyExistsException {
        if (players.containsKey(player.getUsername())) {
            throw new PlayerAlreadyExistsException(player.getUsername());
        }
        players.put(player.getUsername(), player);
    }

    private Player updatePlayerName(String username, String displayName) throws PlayerNotFoundException {
        Player player = find(username);
        player.setDisplayName(displayName);
        return player;
    }

    public Player find(String username) throws PlayerNotFoundException {
        Player player = players.get(username);
        if (player == null) {
            throw new PlayerNotFoundException(username);
        }
        return player;
    }

    public static class PlayerNotFoundException extends ApiMisuseException {
        PlayerNotFoundException(String username) {
            super("Player '" + username + "' doesn't exist");
        }
    }

    private static class PlayerAlreadyExistsException extends ApiMisuseException {
        PlayerAlreadyExistsException(String username) {
            super("Player '" + username + "' already exists");
        }
    }
}

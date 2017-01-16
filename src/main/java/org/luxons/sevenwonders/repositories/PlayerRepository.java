package org.luxons.sevenwonders.repositories;

import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.errors.ApiMisuseException;
import org.luxons.sevenwonders.game.Player;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRepository {

    private Map<String, Player> players = new HashMap<>();

    public boolean contains(String username) {
        return players.containsKey(username);
    }

    public Player createOrUpdate(String username, String displayName) {
        if (players.containsKey(username)) {
            return update(username, displayName);
        } else {
            return create(username, displayName);
        }
    }

    private Player create(String username, String displayName) {
        Player player = new Player(username, displayName);
        players.put(username, player);
        return player;
    }

    private Player update(String username, String displayName) throws PlayerNotFoundException {
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

    public Player remove(String username) {
        Player player = players.remove(username);
        if (player == null) {
            throw new PlayerNotFoundException(username);
        }
        return player;
    }

    static class PlayerNotFoundException extends ApiMisuseException {
        PlayerNotFoundException(String username) {
            super("Player '" + username + "' doesn't exist");
        }
    }
}

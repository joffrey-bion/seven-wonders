package org.luxons.sevenwonders.repositories;

import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.errors.ApiMisuseException;
import org.luxons.sevenwonders.game.Player;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerRepository {

    private Map<String, Player> players = new HashMap<>();

    public Player updateOrCreatePlayer(String userName, String displayName) {
        if (players.containsKey(userName)) {
            return updatePlayerName(userName, displayName);
        } else {
            return createPlayer(userName, displayName);
        }
    }

    private Player createPlayer(String userName, String displayName) throws PlayerAlreadyExistsException {
        Player player = new Player(userName, displayName);
        add(player);
        return player;
    }

    private void add(Player player) throws PlayerAlreadyExistsException {
        if (players.containsKey(player.getUserName())) {
            throw new PlayerAlreadyExistsException(player.getUserName());
        }
        players.put(player.getUserName(), player);
    }

    private Player updatePlayerName(String userName, String displayName) throws PlayerNotFoundException {
        Player player = find(userName);
        player.setDisplayName(displayName);
        return player;
    }

    public Player find(String userName) throws PlayerNotFoundException {
        Player player = players.get(userName);
        if (player == null) {
            throw new PlayerNotFoundException(userName);
        }
        return player;
    }

    public static class PlayerNotFoundException extends ApiMisuseException {
        PlayerNotFoundException(String userName) {
            super("Player '" + userName + "' doesn't exist");
        }
    }

    private static class PlayerAlreadyExistsException extends ApiMisuseException {
        PlayerAlreadyExistsException(String userName) {
            super("Player '" + userName + "' already exists");
        }
    }
}

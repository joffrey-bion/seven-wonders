package org.luxons.sevenwonders.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.repositories.GameRepository;
import org.luxons.sevenwonders.repositories.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DestinationAccessValidator {

    private static final Pattern lobbyDestination = Pattern.compile(".*?/lobby/(?<id>\\d+?)(/.*)?");

    private static final Pattern gameDestination = Pattern.compile(".*?/game/(?<id>\\d+?)(/.*)?");

    private final LobbyRepository lobbyRepository;

    private final GameRepository gameRepository;

    @Autowired
    public DestinationAccessValidator(LobbyRepository lobbyRepository, GameRepository gameRepository) {
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
    }

    public boolean hasAccess(String userName, String destination) {
        if (userName == null) {
            // unnamed user cannot belong to anything
            return false;
        }
        if (hasForbiddenGameReference(userName, destination)) {
            return false;
        }
        if (hasForbiddenLobbyReference(userName, destination)) {
            return false;
        }
        return true;
    }

    private boolean hasForbiddenGameReference(String userName, String destination) {
        Matcher gameMatcher = gameDestination.matcher(destination);
        if (!gameMatcher.matches()) {
            return false; // no game reference is always OK
        }
        int gameId = extractId(gameMatcher);
        return !isUserInGame(userName, gameId);
    }

    private boolean hasForbiddenLobbyReference(String userName, String destination) {
        Matcher lobbyMatcher = lobbyDestination.matcher(destination);
        if (!lobbyMatcher.matches()) {
            return false; // no lobby reference is always OK
        }
        int lobbyId = extractId(lobbyMatcher);
        return !isUserInLobby(userName, lobbyId);
    }

    private boolean isUserInGame(String userName, int gameId) {
        Game game = gameRepository.find(gameId);
        return game.containsUser(userName);
    }

    private boolean isUserInLobby(String userName, int lobbyId) {
        Lobby lobby = lobbyRepository.find(lobbyId);
        return lobby.containsUser(userName);
    }

    private static int extractId(Matcher matcher) {
        String id = matcher.group("id");
        if (id == null) {
            throw new IllegalArgumentException("No id matched in the destination");
        }
        return Integer.parseInt(id);
    }
}

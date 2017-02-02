package org.luxons.sevenwonders.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.luxons.sevenwonders.lobby.Lobby;
import org.luxons.sevenwonders.repositories.LobbyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DestinationAccessValidator {

    private static final Pattern lobbyDestination = Pattern.compile(".*?/lobby/(?<id>\\d+?)(/.*)?");

    private static final Pattern gameDestination = Pattern.compile(".*?/game/(?<id>\\d+?)(/.*)?");

    private final LobbyRepository lobbyRepository;

    @Autowired
    public DestinationAccessValidator(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public boolean hasAccess(String username, String destination) {
        if (username == null) {
            // unnamed user cannot belong to anything
            return false;
        }
        if (hasForbiddenGameReference(username, destination)) {
            return false;
        }
        if (hasForbiddenLobbyReference(username, destination)) {
            return false;
        }
        return true;
    }

    private boolean hasForbiddenGameReference(String username, String destination) {
        Matcher gameMatcher = gameDestination.matcher(destination);
        if (!gameMatcher.matches()) {
            return false; // no game reference is always OK
        }
        int gameId = extractId(gameMatcher);
        return !isUserInLobby(username, gameId);
    }

    private boolean hasForbiddenLobbyReference(String username, String destination) {
        Matcher lobbyMatcher = lobbyDestination.matcher(destination);
        if (!lobbyMatcher.matches()) {
            return false; // no lobby reference is always OK
        }
        int lobbyId = extractId(lobbyMatcher);
        return !isUserInLobby(username, lobbyId);
    }

    private boolean isUserInLobby(String username, int lobbyId) {
        Lobby lobby = lobbyRepository.find(lobbyId);
        return lobby.containsUser(username);
    }

    private static int extractId(Matcher matcher) {
        String id = matcher.group("id");
        return Integer.parseInt(id);
    }
}

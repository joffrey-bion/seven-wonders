package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.actions.JoinOrCreateGameAction;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@MessageMapping("/lobby")
public class LobbyController {

    private static final Logger logger = LoggerFactory.getLogger(LobbyController.class);

    public static final String ATTR_LOBBY = "lobby";

    private final GameDefinitionLoader gameDefinitionLoader;

    private long lastGameId = 0;

    private Map<String, Lobby> lobbies = new HashMap<>();

    private Map<String, Game> games = new HashMap<>();

    @Autowired
    public LobbyController(GameDefinitionLoader gameDefinitionLoader) {
        this.gameDefinitionLoader = gameDefinitionLoader;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(Throwable exception) {
        logger.error("An error occured during message handling", exception);
        return exception.getClass().getSimpleName() + ": " + exception.getMessage();
    }

    @MessageMapping("/create-game")
    @SendTo("/topic/games")
    public Lobby createGame(SimpMessageHeaderAccessor headerAccessor, @Validated JoinOrCreateGameAction action,
            Principal principal) {
        Lobby lobby = (Lobby)headerAccessor.getSessionAttributes().get(ATTR_LOBBY);
        if (lobby != null) {
            logger.warn("Client already in game '{}', cannot create a new game", lobby.getName());
            return lobby;
        }

        Player player = createPlayer(action.getPlayerName(), principal);
        lobby = createGame(action.getGameName(), player);

        headerAccessor.getSessionAttributes().put(ATTR_LOBBY, lobby);

        logger.info("Game '{}' (id={}) created by {} ({})", lobby.getName(), lobby.getId(), player.getDisplayName(),
                player.getUserName());
        return lobby;
    }

    @MessageMapping("/join-game")
    @SendToUser("/queue/join-game")
    public Lobby joinGame(SimpMessageHeaderAccessor headerAccessor, @Validated JoinOrCreateGameAction action,
            Principal principal) {
        Lobby lobby = (Lobby)headerAccessor.getSessionAttributes().get(ATTR_LOBBY);
        if (lobby != null) {
            logger.warn("Client already in game '{}', cannot join a different game", lobby.getName());
            return lobby;
        }

        lobby = lobbies.get(action.getGameName());
        if (lobby == null) {
            throw new GameNotFoundException(action.getGameName());
        }

        Player newPlayer = createPlayer(action.getPlayerName(), principal);
        lobby.addPlayer(newPlayer);

        headerAccessor.getSessionAttributes().put(ATTR_LOBBY, lobby);

        logger.warn("Player {} joined game {}", action.getPlayerName(), action.getGameName());

        return lobby;
    }

    private Player createPlayer(String name, Principal principal) {
        Player player = new Player(name);
        player.setUserName(principal.getName());
        return player;
    }

    private Lobby createGame(String name, Player owner) {
        if (lobbies.containsKey(name)) {
            throw new GameNameAlreadyUsedException(name);
        }
        long id = lastGameId++;
        Lobby lobby = new Lobby(id, name, owner, gameDefinitionLoader.getGameDefinition());
        lobbies.put(name, lobby);
        return lobby;
    }

    private class GameNotFoundException extends RuntimeException {

        public GameNotFoundException(String name) {
            super(name);
        }

    }

    private class GameNameAlreadyUsedException extends UniqueIdAlreadyUsedException {

        public GameNameAlreadyUsedException(String name) {
            super(name);
        }
    }

}

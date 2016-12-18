package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.luxons.sevenwonders.actions.JoinOrCreateGameAction;
import org.luxons.sevenwonders.actions.StartGameAction;
import org.luxons.sevenwonders.errors.UniqueIdAlreadyUsedException;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.luxons.sevenwonders.session.SessionAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@MessageMapping("/lobby")
public class LobbyController {

    private static final Logger logger = LoggerFactory.getLogger(LobbyController.class);

    private final GameDefinitionLoader gameDefinitionLoader;

    private final SimpMessagingTemplate template;

    private long lastGameId = 0;

    private Map<String, Lobby> lobbies = new HashMap<>();

    private Map<String, Game> games = new HashMap<>();

    @Autowired
    public LobbyController(GameDefinitionLoader gameDefinitionLoader, SimpMessagingTemplate template) {
        this.gameDefinitionLoader = gameDefinitionLoader;
        this.template = template;
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
        Lobby lobby = (Lobby)headerAccessor.getSessionAttributes().get(SessionAttributes.ATTR_LOBBY);
        if (lobby != null) {
            logger.warn("Client already in game '{}', cannot create a new game", lobby.getName());
            return lobby;
        }

        Player player = createPlayer(action.getPlayerName(), principal);
        lobby = createGame(action.getGameName(), player);

        headerAccessor.getSessionAttributes().put(SessionAttributes.ATTR_LOBBY, lobby);

        logger.info("Game '{}' (id={}) created by {} ({})", lobby.getName(), lobby.getId(), player.getDisplayName(),
                player.getUserName());
        return lobby;
    }

    @MessageMapping("/join-game")
    @SendToUser("/queue/join-game")
    public Lobby joinGame(SimpMessageHeaderAccessor headerAccessor, @Validated JoinOrCreateGameAction action,
            Principal principal) {
        Lobby lobby = (Lobby)headerAccessor.getSessionAttributes().get(SessionAttributes.ATTR_LOBBY);
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

        headerAccessor.getSessionAttributes().put(SessionAttributes.ATTR_LOBBY, lobby);

        logger.warn("Player {} joined game {}", action.getPlayerName(), action.getGameName());

        return lobby;
    }

    @MessageMapping("/start-game")
    public void startGame(SimpMessageHeaderAccessor headerAccessor, @Validated StartGameAction action,
            Principal principal) {
        Lobby lobby = (Lobby)headerAccessor.getSessionAttributes().get(SessionAttributes.ATTR_LOBBY);
        if (lobby == null) {
            logger.error("User {} is not in a lobby", principal.getName());
            template.convertAndSendToUser(principal.getName(), "/queue/errors", "No game to start");
            return;
        }

        if (!lobby.isOwner(principal.getName())) {
            logger.error("User {} is not the owner of the game '{}'", principal.getName(), lobby.getName());
            template.convertAndSendToUser(principal.getName(), "/queue/errors", "Only the owner can start the game");
            return;
        }

        Game game = lobby.startGame(action.getSettings());
        logger.info("Game {} successfully started", game.getId());

        List<PlayerTurnInfo> playerTurnInfos = game.startTurn();
        for (PlayerTurnInfo playerTurnInfo : playerTurnInfos) {
            Player player = playerTurnInfo.getTable().getPlayers().get(playerTurnInfo.getPlayerIndex());
            String userName = player.getUserName();
            template.convertAndSendToUser(userName, "/queue/game/turn", playerTurnInfo);
        }
    }

    private Player createPlayer(String name, Principal principal) {
        return new Player(name, principal.getName());
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

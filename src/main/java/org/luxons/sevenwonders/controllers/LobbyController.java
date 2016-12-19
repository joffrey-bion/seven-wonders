package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.List;

import org.luxons.sevenwonders.actions.JoinOrCreateGameAction;
import org.luxons.sevenwonders.actions.StartGameAction;
import org.luxons.sevenwonders.errors.ApiMisuseException;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.repositories.GameRepository;
import org.luxons.sevenwonders.repositories.LobbyRepository;
import org.luxons.sevenwonders.session.SessionAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final SimpMessagingTemplate template;

    private final LobbyRepository lobbyRepository;

    private final GameRepository gameRepository;

    @Autowired
    public LobbyController(SimpMessagingTemplate template, LobbyRepository lobbyRepository,
            GameRepository gameRepository) {
        this.template = template;
        this.lobbyRepository = lobbyRepository;
        this.gameRepository = gameRepository;
    }

    @MessageMapping("/create-game")
    @SendTo("/topic/games")
    public Lobby createGame(SimpMessageHeaderAccessor headerAccessor, @Validated JoinOrCreateGameAction action,
            Principal principal) {
        checkThatUserIsNotInAGame(headerAccessor, "cannot create another game");

        Player gameOwner = new Player(action.getPlayerName(), principal.getName());
        Lobby lobby = lobbyRepository.create(action.getGameName(), gameOwner);
        headerAccessor.getSessionAttributes().put(SessionAttributes.ATTR_LOBBY, lobby);

        logger.info("Game '{}' (id={}) created by {} ({})", lobby.getName(), lobby.getId(), gameOwner.getDisplayName(),
                gameOwner.getUserName());
        return lobby;
    }

    @MessageMapping("/join-game")
    @SendToUser("/queue/join-game")
    public Lobby joinGame(SimpMessageHeaderAccessor headerAccessor, @Validated JoinOrCreateGameAction action,
            Principal principal) {
        checkThatUserIsNotInAGame(headerAccessor, "cannot join another game");

        Lobby lobby = lobbyRepository.find(action.getGameName());
        Player newPlayer = new Player(action.getPlayerName(), principal.getName());
        lobby.addPlayer(newPlayer);
        headerAccessor.getSessionAttributes().put(SessionAttributes.ATTR_LOBBY, lobby);

        logger.info("Player {} joined game {}", action.getPlayerName(), action.getGameName());
        return lobby;
    }

    private void checkThatUserIsNotInAGame(SimpMessageHeaderAccessor headerAccessor,
            String impossibleActionDescription) {
        Lobby lobby = (Lobby)headerAccessor.getSessionAttributes().get(SessionAttributes.ATTR_LOBBY);
        if (lobby != null) {
            throw new UserAlreadyInGameException(lobby.getName(), impossibleActionDescription);
        }
    }

    @MessageMapping("/start-game")
    public void startGame(SimpMessageHeaderAccessor headerAccessor, @Validated StartGameAction action,
            Principal principal) {
        Lobby lobby = getOwnedLobby(headerAccessor, principal);
        Game game = lobby.startGame(action.getSettings());
        gameRepository.add(game);

        logger.info("Game {} successfully started", game.getId());

        List<PlayerTurnInfo> playerTurnInfos = game.startTurn();
        for (PlayerTurnInfo playerTurnInfo : playerTurnInfos) {
            Player player = playerTurnInfo.getTable().getPlayers().get(playerTurnInfo.getPlayerIndex());
            String userName = player.getUserName();
            template.convertAndSendToUser(userName, "/queue/game/turn", playerTurnInfo);
        }
    }

    private Lobby getOwnedLobby(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        Lobby lobby = (Lobby)headerAccessor.getSessionAttributes().get(SessionAttributes.ATTR_LOBBY);
        if (lobby == null) {
            throw new UserOwnsNoLobbyException("User " + principal.getName() + " is not in a lobby");
        }
        if (!lobby.isOwner(principal.getName())) {
            throw new UserOwnsNoLobbyException("User " + principal.getName() + " does not own the lobby he's in");
        }
        return lobby;
    }

    private class UserOwnsNoLobbyException extends ApiMisuseException {
        UserOwnsNoLobbyException(String message) {
            super(message);
        }
    }

    private class UserAlreadyInGameException extends ApiMisuseException {
        UserAlreadyInGameException(String gameName, String impossibleActionDescription) {
            super("Client already in game '" + gameName + "', " + impossibleActionDescription);
        }
    }
}

package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

import org.luxons.sevenwonders.actions.CreateGameAction;
import org.luxons.sevenwonders.actions.JoinGameAction;
import org.luxons.sevenwonders.errors.ApiMisuseException;
import org.luxons.sevenwonders.lobby.Lobby;
import org.luxons.sevenwonders.lobby.Player;
import org.luxons.sevenwonders.repositories.LobbyRepository;
import org.luxons.sevenwonders.repositories.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
public class GameBrowserController {

    private static final Logger logger = LoggerFactory.getLogger(GameBrowserController.class);

    private final LobbyController lobbyController;

    private final LobbyRepository lobbyRepository;

    private final PlayerRepository playerRepository;

    private final SimpMessagingTemplate template;

    @Autowired
    public GameBrowserController(LobbyController lobbyController, LobbyRepository lobbyRepository,
            PlayerRepository playerRepository, SimpMessagingTemplate template) {
        this.lobbyController = lobbyController;
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
        this.template = template;
    }

    @SubscribeMapping("/games") // prefix /topic not shown
    public Collection<Lobby> listGames(Principal principal) {
        logger.info("Player '{}' subscribed to /topic/games", principal.getName());
        return lobbyRepository.list();
    }

    @MessageMapping("/lobby/create")
    @SendToUser("/queue/lobby/joined")
    public Lobby createGame(@Validated CreateGameAction action, Principal principal) {
        checkThatUserIsNotInAGame(principal, "cannot create another game");

        Player gameOwner = playerRepository.find(principal.getName());
        Lobby lobby = lobbyRepository.create(action.getGameName(), gameOwner);
        gameOwner.setLobby(lobby);

        logger.info("Game '{}' ({}) created by {} ({})", lobby.getName(), lobby.getId(), gameOwner.getDisplayName(),
                gameOwner.getUsername());

        // notify everyone that a new game exists
        template.convertAndSend("/topic/games", Collections.singletonList(lobby));
        return lobby;
    }

    @MessageMapping("/lobby/join")
    @SendToUser("/queue/lobby/joined")
    public Lobby joinGame(@Validated JoinGameAction action, Principal principal) {
        checkThatUserIsNotInAGame(principal, "cannot join another game");

        Lobby lobby = lobbyRepository.find(action.getGameId());
        Player newPlayer = playerRepository.find(principal.getName());
        lobby.addPlayer(newPlayer);
        newPlayer.setLobby(lobby);

        logger.info("Player '{}' ({}) joined game {}", newPlayer.getDisplayName(), newPlayer.getUsername(),
                lobby.getName());
        lobbyController.sendLobbyUpdateToPlayers(lobby);
        return lobby;
    }

    private void checkThatUserIsNotInAGame(Principal principal, String impossibleActionDescription) {
        Lobby lobby = playerRepository.find(principal.getName()).getLobby();
        if (lobby != null) {
            throw new UserAlreadyInGameException(lobby.getName(), impossibleActionDescription);
        }
    }

    private static class UserAlreadyInGameException extends ApiMisuseException {
        UserAlreadyInGameException(String gameName, String impossibleActionDescription) {
            super("Client already in game '" + gameName + "', " + impossibleActionDescription);
        }
    }
}

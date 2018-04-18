package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

import org.hildan.livedoc.core.annotations.Api;
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

/**
 * This is the place where the player looks for a game.
 */
@Api(name = "GameBrowser")
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

    /**
     * Gets the created or updated games. The list of existing games is received on this topic at once upon
     * subscription, and then each time the list changes.
     *
     * @param principal
     *         the connected user's information
     *
     * @return the current list of {@link Lobby}s
     */
    @SubscribeMapping("/games") // prefix /topic not shown
    public Collection<Lobby> listGames(Principal principal) {
        logger.info("Player '{}' subscribed to /topic/games", principal.getName());
        return lobbyRepository.list();
    }

    /**
     * Creates a new {@link Lobby}.
     *
     * @param action
     *         the action to create the game
     * @param principal
     *         the connected user's information
     *
     * @return the newly created {@link Lobby}
     */
    @MessageMapping("/lobby/create")
    @SendToUser("/queue/lobby/joined")
    public Lobby createGame(@Validated CreateGameAction action, Principal principal) {
        checkThatUserIsNotInAGame(principal, "cannot create another game");

        Player gameOwner = playerRepository.find(principal.getName());
        Lobby lobby = lobbyRepository.create(action.getGameName(), gameOwner);

        logger.info("Game '{}' ({}) created by {} ({})", lobby.getName(), lobby.getId(), gameOwner.getDisplayName(),
                gameOwner.getUsername());

        // notify everyone that a new game exists
        template.convertAndSend("/topic/games", Collections.singletonList(lobby));
        return lobby;
    }

    /**
     * Joins an existing {@link Lobby}.
     *
     * @param action
     *         the action to join the game
     * @param principal
     *         the connected user's information
     *
     * @return the {@link Lobby} that has just been joined
     */
    @MessageMapping("/lobby/join")
    @SendToUser("/queue/lobby/joined")
    public Lobby joinGame(@Validated JoinGameAction action, Principal principal) {
        checkThatUserIsNotInAGame(principal, "cannot join another game");

        Lobby lobby = lobbyRepository.find(action.getGameId());
        Player newPlayer = playerRepository.find(principal.getName());
        lobby.addPlayer(newPlayer);

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

    static class UserAlreadyInGameException extends ApiMisuseException {
        UserAlreadyInGameException(String gameName, String impossibleActionDescription) {
            super("Client already in game '" + gameName + "', " + impossibleActionDescription);
        }
    }
}

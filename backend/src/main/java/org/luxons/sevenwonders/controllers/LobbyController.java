package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

import org.luxons.sevenwonders.actions.ChooseNameAction;
import org.luxons.sevenwonders.actions.CreateGameAction;
import org.luxons.sevenwonders.actions.JoinGameAction;
import org.luxons.sevenwonders.actions.ReorderPlayersAction;
import org.luxons.sevenwonders.actions.UpdateSettingsAction;
import org.luxons.sevenwonders.errors.ApiMisuseException;
import org.luxons.sevenwonders.game.Game;
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
public class LobbyController {

    private static final Logger logger = LoggerFactory.getLogger(LobbyController.class);

    private final LobbyRepository lobbyRepository;

    private final PlayerRepository playerRepository;

    private final SimpMessagingTemplate template;

    @Autowired
    public LobbyController(LobbyRepository lobbyRepository, PlayerRepository playerRepository,
                           SimpMessagingTemplate template) {
        this.lobbyRepository = lobbyRepository;
        this.playerRepository = playerRepository;
        this.template = template;
    }

    @MessageMapping("/chooseName")
    @SendToUser("/queue/nameChoice")
    public Player chooseName(@Validated ChooseNameAction action, Principal principal) {
        String username = principal.getName();
        Player player = playerRepository.createOrUpdate(username, action.getPlayerName());

        logger.info("Player '{}' chose the name '{}'", username, player.getDisplayName());
        return player;
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
        sendLobbyUpdateToPlayers(lobby);
        return lobby;
    }

    private void checkThatUserIsNotInAGame(Principal principal, String impossibleActionDescription) {
        Lobby lobby = playerRepository.find(principal.getName()).getLobby();
        if (lobby != null) {
            throw new UserAlreadyInGameException(lobby.getName(), impossibleActionDescription);
        }
    }

    @MessageMapping("/lobby/reorderPlayers")
    public void reorderPlayers(@Validated ReorderPlayersAction action, Principal principal) {
        Lobby lobby = getLobby(principal);
        lobby.reorderPlayers(action.getOrderedPlayers());

        logger.info("Players in game {} reordered to {}", lobby.getName(), action.getOrderedPlayers());
        sendLobbyUpdateToPlayers(lobby);
    }

    @MessageMapping("/lobby/updateSettings")
    public void updateSettings(@Validated UpdateSettingsAction action, Principal principal) {
        Lobby lobby = getLobby(principal);
        lobby.setSettings(action.getSettings());

        logger.info("Updated settings of game {}", lobby.getName());
        sendLobbyUpdateToPlayers(lobby);
    }

    private void sendLobbyUpdateToPlayers(Lobby lobby) {
        template.convertAndSend("/topic/lobby/" + lobby.getId() + "/updated", lobby);
        template.convertAndSend("/topic/games", Collections.singletonList(lobby));
    }

    @MessageMapping("/lobby/start")
    public void startGame(Principal principal) {
        Lobby lobby = getOwnedLobby(principal);
        Game game = lobby.startGame();

        logger.info("Game {} successfully started", game.getId());
        template.convertAndSend("/topic/lobby/" + lobby.getId() + "/started", (Object) null);
    }

    private Lobby getOwnedLobby(Principal principal) {
        Lobby lobby = getLobby(principal);
        if (!lobby.isOwner(principal.getName())) {
            throw new UserIsNotOwnerException(principal.getName());
        }
        return lobby;
    }

    private Lobby getLobby(Principal principal) {
        Lobby lobby = playerRepository.find(principal.getName()).getLobby();
        if (lobby == null) {
            throw new UserNotInLobbyException(principal.getName());
        }
        return lobby;
    }

    private static class UserNotInLobbyException extends ApiMisuseException {
        UserNotInLobbyException(String username) {
            super("User " + username + " is not in a lobby, create or join a game first");
        }
    }

    private static class UserIsNotOwnerException extends ApiMisuseException {
        UserIsNotOwnerException(String username) {
            super("User " + username + " does not own the lobby he's in");
        }
    }

    private static class UserAlreadyInGameException extends ApiMisuseException {
        UserAlreadyInGameException(String gameName, String impossibleActionDescription) {
            super("Client already in game '" + gameName + "', " + impossibleActionDescription);
        }
    }
}

package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.Collections;

import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiMethod;
import org.luxons.sevenwonders.actions.ReorderPlayersAction;
import org.luxons.sevenwonders.actions.UpdateSettingsAction;
import org.luxons.sevenwonders.errors.ApiMisuseException;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.lobby.Lobby;
import org.luxons.sevenwonders.lobby.Player;
import org.luxons.sevenwonders.repositories.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Api(name = "Lobby", description = "The place where players gather before a game")
@Controller
public class LobbyController {

    private static final Logger logger = LoggerFactory.getLogger(LobbyController.class);

    private final PlayerRepository playerRepository;

    private final SimpMessagingTemplate template;

    @Autowired
    public LobbyController(PlayerRepository playerRepository, SimpMessagingTemplate template) {
        this.playerRepository = playerRepository;
        this.template = template;
    }

    @ApiMethod
    @MessageMapping("/lobby/leave")
    public void leave(Principal principal) {
        Lobby lobby = getLobby(principal);
        lobby.removePlayer(principal.getName());

        logger.info("Player '{}' left game '{}'", principal.getName(), lobby.getName());
        sendLobbyUpdateToPlayers(lobby);
    }

    @ApiMethod
    @MessageMapping("/lobby/reorderPlayers")
    public void reorderPlayers(@Validated ReorderPlayersAction action, Principal principal) {
        Lobby lobby = getOwnedLobby(principal);
        lobby.reorderPlayers(action.getOrderedPlayers());

        logger.info("Players in game '{}' reordered to {}", lobby.getName(), action.getOrderedPlayers());
        sendLobbyUpdateToPlayers(lobby);
    }

    @ApiMethod
    @MessageMapping("/lobby/updateSettings")
    public void updateSettings(@Validated UpdateSettingsAction action, Principal principal) {
        Lobby lobby = getOwnedLobby(principal);
        lobby.setSettings(action.getSettings());

        logger.info("Updated settings of game '{}'", lobby.getName());
        sendLobbyUpdateToPlayers(lobby);
    }

    void sendLobbyUpdateToPlayers(Lobby lobby) {
        template.convertAndSend("/topic/lobby/" + lobby.getId() + "/updated", lobby);
        template.convertAndSend("/topic/games", Collections.singletonList(lobby));
    }

    @ApiMethod
    @MessageMapping("/lobby/startGame")
    public void startGame(Principal principal) {
        Lobby lobby = getOwnedLobby(principal);
        Game game = lobby.startGame();

        logger.info("Game '{}' successfully started", game.getId());
        template.convertAndSend("/topic/lobby/" + lobby.getId() + "/started", "");
    }

    private Lobby getOwnedLobby(Principal principal) {
        Lobby lobby = getLobby(principal);
        if (!lobby.isOwner(principal.getName())) {
            throw new PlayerIsNotOwnerException(principal.getName());
        }
        return lobby;
    }

    private Lobby getLobby(Principal principal) {
        Lobby lobby = getPlayer(principal).getLobby();
        if (lobby == null) {
            throw new PlayerNotInLobbyException(principal.getName());
        }
        return lobby;
    }

    private Player getPlayer(Principal principal) {
        return playerRepository.find(principal.getName());
    }

    static class PlayerNotInLobbyException extends ApiMisuseException {
        PlayerNotInLobbyException(String username) {
            super("User " + username + " is not in a lobby, create or join a game first");
        }
    }

    static class PlayerIsNotOwnerException extends ApiMisuseException {
        PlayerIsNotOwnerException(String username) {
            super("User " + username + " does not own the lobby he's in");
        }
    }
}

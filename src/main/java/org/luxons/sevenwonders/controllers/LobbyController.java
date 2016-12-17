package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.actions.JoinGameAction;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Controller
@MessageMapping("/lobby")
public class LobbyController {

    private static final Logger logger = LoggerFactory.getLogger(LobbyController.class);

    private final GameDefinitionLoader gameDefinitionLoader;

    private long lastGameId = 0;

    private Map<String, Lobby> lobbies = new HashMap<>();

    private Map<String, Game> games = new HashMap<>();

    @Autowired
    public LobbyController(GameDefinitionLoader gameDefinitionLoader) {
        this.gameDefinitionLoader = gameDefinitionLoader;
    }

    @MessageMapping("/create-game")
    @SendTo("/topic/games")
    public String createGame(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        long newId = lastGameId++;
        String id = String.valueOf(newId);

        Lobby lobby = new Lobby(newId, gameDefinitionLoader.getGameDefinition());
        lobbies.put(id, lobby);
        logger.info("Game {} created by {}", id, principal.getName());
        return id;
    }

    @MessageMapping("/join-game")
    @SendTo("/topic/players")
    public Player joinGame(SimpMessageHeaderAccessor headerAccessor, @Validated JoinGameAction action,
            Principal principal) {
        Player player = (Player)headerAccessor.getSessionAttributes().get("player");
        Lobby lobby = (Lobby)headerAccessor.getSessionAttributes().get("lobby");
        if (player != null && lobby != null) {
            logger.warn("Client has already joined game {} under the name {}", lobby.getId(), player.getName());
            return player;
        }

        lobby = lobbies.get(action.getGameId());
        Player newPlayer = new Player(action.getPlayerName());
        newPlayer.setUserName(principal.getName());
        lobby.addPlayer(newPlayer);

        headerAccessor.getSessionAttributes().put("player", newPlayer);
        headerAccessor.getSessionAttributes().put("lobby", lobby);

        logger.warn("Player {} joined game {}", action.getPlayerName(), action.getGameId());

        return newPlayer;
    }
}

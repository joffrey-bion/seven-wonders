package org.luxons.sevenwonders.controllers;

import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.actions.JoinGameAction;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Lobby;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.data.GameDefinitionLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
//@MessageMapping("/lobby")
public class LobbyController {

    private final GameDefinitionLoader gameDefinitionLoader;

    private long lastGameId = 0;

    private Map<String, Lobby> lobbies = new HashMap<>();

    private Map<String, Game> games = new HashMap<>();

    @Autowired
    public LobbyController(GameDefinitionLoader gameDefinitionLoader) {
        this.gameDefinitionLoader = gameDefinitionLoader;
    }

    @MessageMapping("/create-game")
    @SendTo("/broadcast/games")
    public String createGame(SimpMessageHeaderAccessor headerAccessor) throws Exception {
        System.out.println("Received message: " + headerAccessor.getSessionId());
        Thread.sleep(1000); // simulated delay

        String id = String.valueOf(lastGameId++);
        System.out.println("Creating game " + id);

        Lobby lobby = new Lobby(lastGameId, gameDefinitionLoader.getGameDefinition());
        lobbies.put(id, lobby);
        return id;
    }

    @MessageMapping("/join-game")
    @SendTo("/broadcast/players")
    public Player joinGame(SimpMessageHeaderAccessor headerAccessor, JoinGameAction joinAction) throws Exception {
        Thread.sleep(1000); // simulated delay

        Player player = (Player)headerAccessor.getSessionAttributes().get("player");
        Lobby lobby = (Lobby)headerAccessor.getSessionAttributes().get("lobby");
        if (player != null && lobby != null) {
            System.out.println("Client has already joined game " + lobby.getId() + "under the name " + player.getName());
            return player;
        }
        System.out.println("Player " + joinAction.getPlayerName() + " joined game " + joinAction.getGameId());

        lobby = lobbies.get(joinAction.getGameId());
        Player newPlayer = new Player(joinAction.getPlayerName());
        lobby.addPlayer(newPlayer);

        headerAccessor.getSessionAttributes().put("player", newPlayer);
        headerAccessor.getSessionAttributes().put("lobby", lobby);

        return newPlayer;
    }
}

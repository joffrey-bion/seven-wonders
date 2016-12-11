package org.luxons.sevenwonders.controllers;

import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.actions.JoinGameAction;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.Settings;
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

        Settings settings = new Settings();
        Game game = new Game(settings, GameDefinitionLoader.load().create(settings));
        games.put(id, game);
        return id;
    }

    @MessageMapping("/join-game")
    @SendTo("/broadcast/players")
    public Player joinGame(SimpMessageHeaderAccessor headerAccessor, JoinGameAction joinAction) throws Exception {
        Thread.sleep(1000); // simulated delay

        Player player = (Player)headerAccessor.getSessionAttributes().get("player");
        if (player != null) {
            System.out.println("Client has already joined game under the name " + player.getName());
            return player;
        }
        System.out.println("Player " + joinAction.getPlayerName() + " joined game " + joinAction.getGameId());

        Game game = games.get(joinAction.getGameId());
        Player newPlayer = new Player(joinAction.getPlayerName());
        game.addPlayer(newPlayer);

        headerAccessor.getSessionAttributes().put("player", newPlayer);

        return newPlayer;
    }
}

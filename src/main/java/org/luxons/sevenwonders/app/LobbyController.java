package org.luxons.sevenwonders.app;

import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.app.actions.JoinGameAction;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.Settings;
import org.luxons.sevenwonders.game.data.GameDataLoader;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class LobbyController {

    private long lastGameId = 0;

    private Map<String, Game> games = new HashMap<>();

    @MessageMapping("/lobby/create-game")
    @SendTo("/broadcast/games")
    public String createGame() throws Exception {
        Thread.sleep(1000); // simulated delay

        String id = String.valueOf(lastGameId++);
        System.out.println("Creating game " + id);

        Game game = new Game(new Settings(), GameDataLoader.load());
        games.put(id, game);
        return id;
    }

    @MessageMapping("/lobby/join-game")
    @SendTo("/broadcast/players")
    public Player joinGame(JoinGameAction joinAction) throws Exception {
        Thread.sleep(1000); // simulated delay

        System.out.println("Player " + joinAction.getPlayerName() + " joined game " + joinAction.getGameId());

        Game game = games.get(joinAction.getGameId());
        Player player = new Player(joinAction.getPlayerName());
        game.addPlayer(player);
        return player;
    }
}

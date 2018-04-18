package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.List;

import org.hildan.livedoc.core.annotations.Api;
import org.luxons.sevenwonders.actions.PrepareMoveAction;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.cards.CardBack;
import org.luxons.sevenwonders.lobby.Lobby;
import org.luxons.sevenwonders.lobby.Player;
import org.luxons.sevenwonders.output.PreparedCard;
import org.luxons.sevenwonders.repositories.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * This API is for in-game events management.
 */
@Api(name = "Game")
@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final SimpMessagingTemplate template;

    private final PlayerRepository playerRepository;

    @Autowired
    public GameController(SimpMessagingTemplate template, PlayerRepository playerRepository) {
        this.template = template;
        this.playerRepository = playerRepository;
    }

    /**
     * Notifies the game that the player is ready to receive his hand.
     *
     * @param principal
     *         the connected user's information
     */
    @MessageMapping("/game/sayReady")
    public void ready(Principal principal) {
        Player player = playerRepository.find(principal.getName());
        player.setReady(true);
        Game game = player.getGame();
        logger.info("Game {}: player {} is ready for the next turn", game.getId(), player);

        Lobby lobby = player.getLobby();
        List<Player> players = lobby.getPlayers();

        boolean allReady = players.stream().allMatch(Player::isReady);
        if (allReady) {
            logger.info("Game {}: all players ready, sending turn info", game.getId());
            players.forEach(p -> p.setReady(false));
            sendTurnInfo(players, game);
        } else {
            sendPlayerReady(game.getId(), player);
        }
    }

    private void sendTurnInfo(List<Player> players, Game game) {
        for (PlayerTurnInfo turnInfo : game.getCurrentTurnInfo()) {
            Player player = players.get(turnInfo.getPlayerIndex());
            template.convertAndSendToUser(player.getUsername(), "/queue/game/turn", turnInfo);
        }
    }

    private void sendPlayerReady(long gameId, Player player) {
        template.convertAndSend("/topic/game/" + gameId + "/playerReady", player.getUsername());
    }

    /**
     * Prepares the player's next move. When all players have prepared their moves, all moves are executed.
     *
     * @param action
     *         the action to prepare the move
     * @param principal
     *         the connected user's information
     */
    @MessageMapping("/game/prepareMove")
    public void prepareMove(PrepareMoveAction action, Principal principal) {
        Player player = playerRepository.find(principal.getName());
        Game game = player.getGame();
        CardBack preparedCardBack = game.prepareMove(player.getIndex(), action.getMove());
        PreparedCard preparedCard = new PreparedCard(player, preparedCardBack);
        logger.info("Game {}: player {} prepared move {}", game.getId(), principal.getName(), action.getMove());

        if (game.allPlayersPreparedTheirMove()) {
            logger.info("Game {}: all players have prepared their move, executing turn...", game.getId());
            Table table = game.playTurn();
            sendPlayedMoves(game.getId(), table);
        } else {
            sendPreparedCard(game.getId(), preparedCard);
        }
    }

    private void sendPlayedMoves(long gameId, Table table) {
        template.convertAndSend("/topic/game/" + gameId + "/tableUpdates", table);
    }

    private void sendPreparedCard(long gameId, PreparedCard preparedCard) {
        template.convertAndSend("/topic/game/" + gameId + "/prepared", preparedCard);
    }
}

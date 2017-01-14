package org.luxons.sevenwonders.controllers;

import java.security.Principal;
import java.util.List;

import org.luxons.sevenwonders.actions.PrepareCardAction;
import org.luxons.sevenwonders.game.Game;
import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.api.PlayerTurnInfo;
import org.luxons.sevenwonders.game.api.PreparedCard;
import org.luxons.sevenwonders.repositories.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final SimpMessagingTemplate template;

    private final GameRepository gameRepository;

    @Autowired
    public GameController(SimpMessagingTemplate template, GameRepository gameRepository) {
        this.template = template;
        this.gameRepository = gameRepository;
    }

    @MessageMapping("/game/{gameId}/prepare")
    public void prepareCard(@DestinationVariable long gameId, PrepareCardAction action, Principal principal) {
        Game game = gameRepository.find(gameId);
        PreparedCard preparedCard = game.prepareCard(principal.getName(), action.getMove());
        logger.info("Game '{}': player {} prepared move {}", gameId, principal.getName(), action.getMove());

        if (game.areAllPlayersReady()) {
            game.playTurn();
            sendTurnInfo(game);
        } else {
            sendPreparedCard(preparedCard, game);
        }
    }

    private void sendPreparedCard(PreparedCard preparedCard, Game game) {
        for (Player player : game.getPlayers()) {
            String username = player.getUsername();
            template.convertAndSendToUser(username, "/topic/game/" + game.getId() + "/prepared", preparedCard);
        }
    }

    private void sendTurnInfo(Game game) {
        List<PlayerTurnInfo> turnInfos = game.getTurnInfo();
        for (PlayerTurnInfo turnInfo : turnInfos) {
            String username = turnInfo.getPlayer().getUsername();
            template.convertAndSendToUser(username, "/topic/game/" + game.getId() + "/turn", turnInfo);
        }
    }
}

package org.luxons.sevenwonders.server.controllers

import org.hildan.livedoc.core.annotations.Api
import org.luxons.sevenwonders.engine.Game
import org.luxons.sevenwonders.model.Action
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.actions.PrepareMoveAction
import org.luxons.sevenwonders.model.cards.PreparedCard
import org.luxons.sevenwonders.server.api.toDTO
import org.luxons.sevenwonders.server.lobby.Player
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import java.security.Principal

/**
 * This API is for in-game events management.
 */
@Api(name = "Game")
@Controller
class GameController @Autowired constructor(
    private val template: SimpMessagingTemplate,
    private val playerRepository: PlayerRepository
) {
    private val Principal.player
        get() = playerRepository.find(name)

    /**
     * Notifies the game that the player is ready to receive his hand.
     *
     * @param principal
     * the connected user's information
     */
    @MessageMapping("/game/sayReady")
    fun ready(principal: Principal) {
        val player = principal.player
        player.isReady = true
        val game = player.game
        logger.info("Game {}: player {} is ready for the next turn", game.id, player)

        val players = player.lobby.getPlayers()

        sendPlayerReady(game.id, player)

        val allReady = players.all { it.isReady }
        if (allReady) {
            logger.info("Game {}: all players ready, sending turn info", game.id)
            players.forEach { it.isReady = false }
            sendTurnInfo(players, game, false)
        }
    }

    /**
     * Prepares the player's next move. When all players have prepared their moves, all moves are executed.
     *
     * @param action
     * the action to prepare the move
     * @param principal
     * the connected user's information
     */
    @MessageMapping("/game/prepareMove")
    fun prepareMove(action: PrepareMoveAction, principal: Principal) {
        val player = principal.player
        val game = player.game
        val preparedCardBack = game.prepareMove(player.index, action.move)
        val preparedCard = PreparedCard(player.toDTO(), preparedCardBack)
        logger.info("Game {}: player {} prepared move {}", game.id, principal.name, action.move)
        sendPreparedCard(game.id, preparedCard)

        if (game.allPlayersPreparedTheirMove()) {
            logger.info("Game {}: all players have prepared their move, executing turn...", game.id)
            game.playTurn()
            sendTurnInfo(player.lobby.getPlayers(), game, true)
        }
    }

    private fun sendPlayerReady(gameId: Long, player: Player) =
            template.convertAndSend("/topic/game/$gameId/playerReady", "\"${player.username}\"")

    private fun sendPreparedCard(gameId: Long, preparedCard: PreparedCard) =
            template.convertAndSend("/topic/game/$gameId/prepared", preparedCard)

    private fun sendTurnInfo(players: List<Player>, game: Game, hideHands: Boolean) {
        val turns = game.getCurrentTurnInfo()
        val turnsToSend = if (hideHands) turns.hideHandsAndWaitForReadiness() else turns
        for (turnInfo in turnsToSend) {
            val player = players[turnInfo.playerIndex]
            template.convertAndSendToUser(player.username, "/queue/game/turn", turnInfo)
        }
    }

    private fun Collection<PlayerTurnInfo>.hideHandsAndWaitForReadiness() =
            map { it.copy(action = Action.SAY_READY, hand = null) }

    companion object {
        private val logger = LoggerFactory.getLogger(GameController::class.java)
    }
}

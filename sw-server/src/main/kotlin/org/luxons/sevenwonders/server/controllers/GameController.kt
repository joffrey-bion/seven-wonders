package org.luxons.sevenwonders.server.controllers

import org.luxons.sevenwonders.engine.Game
import org.luxons.sevenwonders.model.api.actions.PrepareMoveAction
import org.luxons.sevenwonders.model.cards.PreparedCard
import org.luxons.sevenwonders.model.hideHandsAndWaitForReadiness
import org.luxons.sevenwonders.server.lobby.Player
import org.luxons.sevenwonders.server.repositories.LobbyRepository
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Controller
import java.security.Principal

/**
 * This API is for in-game events management.
 */
@Controller
class GameController(
    private val template: SimpMessagingTemplate,
    private val playerRepository: PlayerRepository,
    private val lobbyRepository: LobbyRepository,
) {
    private val Principal.player
        get() = playerRepository.find(name)

    /**
     * Notifies the game that the player is ready to receive his hand.
     *
     * @param principal the connected user's information
     */
    @MessageMapping("/game/sayReady")
    fun ready(principal: Principal) {
        val player = principal.player
        val lobby = player.lobby
        if (!lobby.settings.askForReadiness) {
            logger.warn("Game {}: player {} is saying ready but readiness concept is disabled", lobby.id, player)
            return
        }

        val game = player.game
        player.isReady = true

        synchronized(lobby) {
            val players = lobby.getPlayers()

            sendPlayerReady(game.id, player)
            logger.info("Game {}: player {} is ready for the next turn", game.id, player)

            val allReady = players.all { it.isReady }
            if (allReady) {
                logger.info("Game {}: all players ready, sending turn info", game.id)
                players.forEach { it.isReady = false }
                sendTurnInfo(players, game, false)
            }
        }
    }

    /**
     * Prepares the player's next move. When all players have prepared their moves, all moves are executed.
     *
     * @param action the action to prepare the move
     * @param principal the connected user's information
     */
    @MessageMapping("/game/prepareMove")
    fun prepareMove(action: PrepareMoveAction, principal: Principal) {
        val player = principal.player
        val lobby = player.lobby
        val game = player.game
        synchronized(game) {
            val preparedCardBack = game.prepareMove(player.index, action.move)
            val preparedCard = PreparedCard(player.username, preparedCardBack)
            logger.info("Game {}: player {} prepared move {}", game.id, player, action.move)
            sendPreparedCard(game.id, preparedCard)

            if (game.allPlayersPreparedTheirMove()) {
                logger.info("Game {}: all players have prepared their move, executing turn...", game.id)
                game.playTurn()
                sendTurnInfo(player.lobby.getPlayers(), game, hideHands = lobby.settings.askForReadiness)
                if (game.endOfGameReached()) {
                    logger.info("Game {}: end of game, displaying score board", game.id)
                    player.lobby.setEndOfGame()
                }
            } else {
                template.convertAndSendToUser(player.username, "/queue/game/preparedMove", action.move)
            }
        }
    }

    @MessageMapping("/game/unprepareMove")
    fun unprepareMove(principal: Principal) {
        val player = principal.player
        val game = player.game
        synchronized(game) {
            game.unprepareMove(player.index)
        }
        val preparedCard = PreparedCard(player.username, null)
        logger.info("Game {}: player {} unprepared his move", game.id, player)
        sendPreparedCard(game.id, preparedCard)
    }

    private fun sendPlayerReady(gameId: Long, player: Player) =
        template.convertAndSend("/topic/game/$gameId/playerReady", player.username)

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

    @MessageMapping("/game/leave")
    fun leave(principal: Principal) {
        val player = principal.player
        val game = player.game
        val lobby = player.lobby
        lobby.removePlayer(player.username)
        logger.info("Game {}: player {} left the game", game.id, player)
        if (lobby.getPlayers().isEmpty()) {
            lobbyRepository.remove(lobby.id)
            logger.info("Game {}: game deleted", game.id)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GameController::class.java)
    }
}

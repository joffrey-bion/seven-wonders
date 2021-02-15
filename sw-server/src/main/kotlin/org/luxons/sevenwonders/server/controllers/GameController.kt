package org.luxons.sevenwonders.server.controllers

import io.micrometer.core.instrument.MeterRegistry
import org.luxons.sevenwonders.engine.Game
import org.luxons.sevenwonders.model.api.GameListEvent
import org.luxons.sevenwonders.model.api.actions.PrepareMoveAction
import org.luxons.sevenwonders.model.api.wrap
import org.luxons.sevenwonders.model.cards.PreparedCard
import org.luxons.sevenwonders.model.hideHandsAndWaitForReadiness
import org.luxons.sevenwonders.server.api.toDTO
import org.luxons.sevenwonders.server.lobby.Lobby
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
    private val meterRegistry: MeterRegistry,
) {
    private val Principal.player
        get() = playerRepository.get(name)

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

        // This lock doesn't have a clear rationale, but it's cleaner to check the readiness state of everyone within a
        // lock together with the code that updates the readiness status, to avoid interleaving surprises.
        synchronized(game) {
            player.isReady = true
            sendPlayerReady(game.id, player)
            logger.info("Game {}: player {} is ready for the next turn", game.id, player)

            val players = lobby.getPlayers()
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

        // We need this lock for multiple reasons:
        // 1. we don't want the player to be able to unprepare his card between prepareMove and sendPreparedCard,
        //    because other players may receive the "unprepared" update before the "prepared" and thus end up still
        //    seeing the prepared card.
        // 2. we don't want a player to unprepare a card between allPlayersPreparedTheirMove and playTurn, because the
        //    error thrown by playTurn would be received by the current (preparing) player instead of the player
        //    unpreparing his card, which would be confusing because the card preparation is a success in this case.
        //    It is therefore better for the player who unprepares his card to wait for the lock and then get rejected
        //    because the turn actually happened.
        // 3. we don't want a player to prepare a card between the preparation and the turn-end check, because the check
        //    for the current player would then take into account that other card and maybe play the turn, which means
        //    that we would run the turn-end check of the other player right after this one. This seems benign but it
        //    actually doesn't respect the implicit assumption that a move in the current turn has just been prepared
        //    before the check. It shouldn't cause harm at the moment but could be harmful in the future.
        // 4. we don't want this code to run in the middle of unprepareMove's own lock
        synchronized(game) {
            logger.info("Game {}: player {} preparing move {}", game.id, player, action.move)
            val preparedCardBack = game.prepareMove(player.index, action.move)
            val preparedCard = PreparedCard(player.username, preparedCardBack)
            sendPreparedCard(game.id, preparedCard)

            if (game.allPlayersPreparedTheirMove()) {
                logger.info("Game {}: all players have prepared their move, executing turn...", game.id)
                game.playTurn()
                sendTurnInfo(player.lobby.getPlayers(), game, hideHands = lobby.settings.askForReadiness)
                if (game.endOfGameReached()) {
                    handleEndOfGame(game, player, lobby)
                }
            } else {
                template.convertAndSendToUser(player.username, "/queue/game/preparedMove", action.move)
            }
        }
    }

    private fun handleEndOfGame(game: Game, player: Player, lobby: Lobby) {
        meterRegistry.counter("games.finished").increment()
        logger.info("Game {}: end of game, displaying score board", game.id)
        player.lobby.setEndOfGame()
        template.convertAndSend("/topic/games", GameListEvent.CreateOrUpdate(lobby.toDTO()).wrap())
    }

    @MessageMapping("/game/unprepareMove")
    fun unprepareMove(principal: Principal) {
        val player = principal.player
        val game = player.game

        // We don't want the player to be able to prepare a card between unprepareMove and sendPreparedCard(null),
        // otherwise other players may receive the "prepared" update before the "unprepared" and thus end up not seeing
        // the prepared card. Note that this protection also requires the lock inside prepareMove.
        synchronized(game) {
            game.unprepareMove(player.index)
            val preparedCard = PreparedCard(player.username, null)
            logger.info("Game {}: player {} unprepared his move", game.id, player)
            sendPreparedCard(game.id, preparedCard)
        }
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

        // This lock is for multiple reasons:
        // 1. this ensures we don't remove players while the last sendTurnInfo is still notifying other players
        // 2. we don't want another player to leave between removePlayer and the lobby deletion check, because if he's
        //    the last player, we would delete the game here, and he would also try to delete the game a second time.
        synchronized(game) {
            lobby.removePlayer(player.username)
            logger.info("Game {}: player {} left the game", game.id, player)
            template.convertAndSendToUser(player.username, "/queue/lobby/left", lobby.id)

            // This could cause problems if the humans are faster than bots to leave a finished game,
            // but this case should be quite rare, so it does not matter much
            if (lobby.getPlayers().none { it.isHuman }) {
                lobbyRepository.remove(lobby.id)
                template.convertAndSend("/topic/games", GameListEvent.Delete(lobby.id).wrap())
                logger.info("Game {}: game deleted", game.id)
            }
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(GameController::class.java)
    }
}

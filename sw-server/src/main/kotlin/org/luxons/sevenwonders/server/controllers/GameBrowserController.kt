package org.luxons.sevenwonders.server.controllers

import org.luxons.sevenwonders.model.api.actions.CreateGameAction
import org.luxons.sevenwonders.model.api.actions.JoinGameAction
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.model.api.events.GameListEvent
import org.luxons.sevenwonders.model.api.events.GameListEventWrapper
import org.luxons.sevenwonders.model.api.events.wrap
import org.luxons.sevenwonders.server.ApiMisuseException
import org.luxons.sevenwonders.server.api.toDTO
import org.luxons.sevenwonders.server.lobby.Lobby
import org.luxons.sevenwonders.server.lobby.Player
import org.luxons.sevenwonders.server.repositories.LobbyRepository
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import java.security.Principal

/**
 * This is the place where the player looks for a game.
 */
@Controller
class GameBrowserController(
    private val messenger: SimpMessageSendingOperations,
    private val lobbyController: LobbyController,
    private val lobbyRepository: LobbyRepository,
    private val playerRepository: PlayerRepository,
) {
    /**
     * Gets the created or updated games. The list of existing games is received on this topic at once upon
     * subscription, and then each time the list changes.
     *
     * @param principal the connected user's information
     *
     * @return the current list of [Lobby]s
     */
    @SubscribeMapping("/games") // prefix /topic not shown
    fun listGames(principal: Principal): GameListEventWrapper {
        logger.info("Player '{}' subscribed to /topic/games", principal.name)
        return GameListEvent.ReplaceList(lobbyRepository.list().map { it.toDTO() }).wrap()
    }

    /**
     * Creates a new [Lobby].
     *
     * @param action the action to create the game
     * @param principal the connected user's information
     *
     * @return the newly created [Lobby]
     */
    @MessageMapping("/lobby/create")
    fun createGame(@Validated action: CreateGameAction, principal: Principal) {
        checkThatUserIsNotInAGame(principal, "cannot create another game")

        val player = playerRepository.get(principal.name)
        val lobby = lobbyRepository.create(action.gameName, owner = player)

        logger.info("Game '{}' ({}) created by {} ({})", lobby.name, lobby.id, player.displayName, player.username)

        // notify everyone that a new game exists
        val lobbyDto = lobby.toDTO()
        messenger.sendGameListEvent(GameListEvent.CreateOrUpdate(lobbyDto))
        messenger.sendGameEvent(player, GameEvent.LobbyJoined(lobbyDto))
    }

    /**
     * Joins an existing [Lobby].
     *
     * @param action the action to join the game
     * @param principal the connected user's information
     *
     * @return the [Lobby] that has just been joined
     */
    @MessageMapping("/lobby/join")
    fun joinGame(@Validated action: JoinGameAction, principal: Principal) {
        checkThatUserIsNotInAGame(principal, "cannot join another game")

        val lobby = lobbyRepository.get(action.gameId)
        val player = playerRepository.get(principal.name)
        synchronized(lobby) {
            lobby.addPlayer(player)

            logger.info("Player '{}' ({}) joined game {}", player.displayName, player.username, lobby.name)
            lobbyController.sendLobbyUpdateToPlayers(lobby)
        }
        messenger.sendGameEvent(player, GameEvent.LobbyJoined(lobby.toDTO()))
    }

    private fun checkThatUserIsNotInAGame(principal: Principal, impossibleActionDescription: String) {
        val player = playerRepository.get(principal.name)
        if (player.isInLobby || player.isInGame) {
            throw UserAlreadyInGameException(player, impossibleActionDescription)
        }
    }

    internal class UserAlreadyInGameException(player: Player, impossibleActionDescription: String) :
        ApiMisuseException("Player $player is already in game '${player.lobby.name}', $impossibleActionDescription")

    companion object {
        private val logger = LoggerFactory.getLogger(GameBrowserController::class.java)
    }
}

package org.luxons.sevenwonders.controllers

import org.hildan.livedoc.core.annotations.Api
import org.luxons.sevenwonders.actions.CreateGameAction
import org.luxons.sevenwonders.actions.JoinGameAction
import org.luxons.sevenwonders.api.LobbyDTO
import org.luxons.sevenwonders.api.toDTO
import org.luxons.sevenwonders.errors.ApiMisuseException
import org.luxons.sevenwonders.lobby.Lobby
import org.luxons.sevenwonders.repositories.LobbyRepository
import org.luxons.sevenwonders.repositories.PlayerRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.messaging.simp.annotation.SubscribeMapping
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import java.security.Principal

/**
 * This is the place where the player looks for a game.
 */
@Api(name = "GameBrowser")
@Controller
class GameBrowserController @Autowired constructor(
    private val lobbyController: LobbyController,
    private val lobbyRepository: LobbyRepository,
    private val playerRepository: PlayerRepository,
    private val template: SimpMessagingTemplate
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
    fun listGames(principal: Principal): Collection<LobbyDTO> {
        logger.info("Player '{}' subscribed to /topic/games", principal.name)
        return lobbyRepository.list().map { it.toDTO(principal.name) }
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
    @SendToUser("/queue/lobby/joined")
    fun createGame(@Validated action: CreateGameAction, principal: Principal): LobbyDTO {
        checkThatUserIsNotInAGame(principal, "cannot create another game")

        val gameOwner = playerRepository.find(principal.name)
        val lobby = lobbyRepository.create(action.gameName, gameOwner)

        logger.info(
            "Game '{}' ({}) created by {} ({})", lobby.name, lobby.id, gameOwner.displayName, gameOwner.username
        )

        // notify everyone that a new game exists
        val lobbyDto = lobby.toDTO(principal.name)
        template.convertAndSend("/topic/games", listOf(lobbyDto))
        return lobbyDto
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
    @SendToUser("/queue/lobby/joined")
    fun joinGame(@Validated action: JoinGameAction, principal: Principal): LobbyDTO {
        checkThatUserIsNotInAGame(principal, "cannot join another game")

        val lobby = lobbyRepository.find(action.gameId)
        val newPlayer = playerRepository.find(principal.name)
        lobby.addPlayer(newPlayer)

        logger.info(
            "Player '{}' ({}) joined game {}", newPlayer.displayName, newPlayer.username, lobby.name
        )
        val lobbyDTO = lobby.toDTO(principal.name)
        lobbyController.sendLobbyUpdateToPlayers(lobbyDTO)
        return lobbyDTO
    }

    private fun checkThatUserIsNotInAGame(principal: Principal, impossibleActionDescription: String) {
        val player = playerRepository.find(principal.name)
        if (player.isInLobby || player.isInGame) {
            throw UserAlreadyInGameException(player.lobby.name, impossibleActionDescription)
        }
    }

    internal class UserAlreadyInGameException(gameName: String, impossibleActionDescription: String) :
        ApiMisuseException("Client already in game '$gameName', $impossibleActionDescription")

    companion object {
        private val logger = LoggerFactory.getLogger(GameBrowserController::class.java)
    }
}

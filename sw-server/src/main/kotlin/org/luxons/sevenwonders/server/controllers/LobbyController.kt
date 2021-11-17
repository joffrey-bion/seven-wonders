package org.luxons.sevenwonders.server.controllers

import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.luxons.sevenwonders.bot.connectBot
import org.luxons.sevenwonders.client.SevenWondersClient
import org.luxons.sevenwonders.model.api.actions.AddBotAction
import org.luxons.sevenwonders.model.api.actions.ReassignWondersAction
import org.luxons.sevenwonders.model.api.actions.ReorderPlayersAction
import org.luxons.sevenwonders.model.api.actions.UpdateSettingsAction
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.model.api.events.GameListEvent
import org.luxons.sevenwonders.model.hideHandsAndWaitForReadiness
import org.luxons.sevenwonders.server.api.toDTO
import org.luxons.sevenwonders.server.lobby.Lobby
import org.luxons.sevenwonders.server.lobby.Player
import org.luxons.sevenwonders.server.metrics.playerCountsTags
import org.luxons.sevenwonders.server.repositories.LobbyRepository
import org.luxons.sevenwonders.server.repositories.PlayerRepository
import org.luxons.sevenwonders.server.utils.CoroutineScopedComponent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import java.security.Principal
import kotlin.time.Duration.Companion.milliseconds

/**
 * Handles actions in the game's lobby. The lobby is the place where players gather before a game.
 */
@Controller
class LobbyController(
    private val messenger: SimpMessageSendingOperations,
    private val lobbyRepository: LobbyRepository,
    private val playerRepository: PlayerRepository,
    @Value("\${server.port}") private val serverPort: String,
    private val meterRegistry: MeterRegistry,
) : CoroutineScopedComponent() {
    private val Principal.player: Player
        get() = playerRepository.get(name)

    /**
     * Leaves the current lobby.
     *
     * @param principal the connected user's information
     */
    @MessageMapping("/lobby/leave")
    fun leave(principal: Principal) {
        val player = principal.player
        val lobby = player.lobby

        synchronized(lobby) {
            lobby.removePlayer(principal.name)
            logger.info("Player {} left the lobby of game '{}'", player, lobby.name)
            messenger.sendGameEvent(player, GameEvent.LobbyLeft)

            if (lobby.getPlayers().none { it.isHuman }) {
                deleteLobby(lobby)
            } else {
                sendLobbyUpdateToPlayers(lobby)
            }
        }
    }

    /**
     * Disbands the current group, making everyone leave the lobby.
     *
     * @param principal the connected user's information
     */
    @MessageMapping("/lobby/disband")
    fun disband(principal: Principal) {
        val player = principal.player
        val lobby = player.ownedLobby

        synchronized(lobby) {
            lobby.getPlayers().forEach {
                it.leave()
                messenger.sendGameEvent(it, GameEvent.LobbyLeft)
            }
            logger.info("Player {} disbanded game '{}'", player, lobby.name)
            deleteLobby(lobby)
            meterRegistry.counter("games.disbanded", lobby.playerCountsTags()).increment()
        }
    }

    private fun deleteLobby(lobby: Lobby) {
        lobbyRepository.remove(lobby.id)
        messenger.sendGameListEvent(GameListEvent.Delete(lobby.id))
        logger.info("Game '{}' removed", lobby.name)
    }

    /**
     * Reorders the players in the current lobby. This can only be done by the lobby's owner.
     *
     * @param action the action to reorder the players
     * @param principal the connected user's information
     */
    @MessageMapping("/lobby/reorderPlayers")
    fun reorderPlayers(@Validated action: ReorderPlayersAction, principal: Principal) {
        val lobby = principal.player.ownedLobby

        synchronized(lobby) {
            lobby.reorderPlayers(action.orderedPlayers)
            logger.info("Players in game '{}' reordered to {}", lobby.name, action.orderedPlayers)
            sendLobbyUpdateToPlayers(lobby)
        }
    }

    /**
     * Reassigns the wonders in the current lobby. This can only be done by the lobby's owner.
     *
     * @param action the action to reassign the wonders
     * @param principal the connected user's information
     */
    @MessageMapping("/lobby/reassignWonders")
    fun reassignWonders(@Validated action: ReassignWondersAction, principal: Principal) {
        val lobby = principal.player.ownedLobby

        synchronized(lobby) {
            lobby.reassignWonders(action.assignedWonders)
            logger.info("Reassigned wonders in game '{}': {}", lobby.name, action.assignedWonders)
            sendLobbyUpdateToPlayers(lobby)
        }
    }

    /**
     * Updates the game settings. This can only be done by the lobby's owner.
     *
     * @param action the action to update the settings
     * @param principal the connected user's information
     */
    @MessageMapping("/lobby/updateSettings")
    fun updateSettings(@Validated action: UpdateSettingsAction, principal: Principal) {
        val lobby = principal.player.ownedLobby

        synchronized(lobby) {
            lobby.settings = action.settings
            logger.info("Updated settings of game '{}'", lobby.name)
            sendLobbyUpdateToPlayers(lobby)
        }
    }

    internal fun sendLobbyUpdateToPlayers(lobby: Lobby) {
        val lobbyDto = lobby.toDTO()
        lobby.getPlayers().forEach {
            messenger.sendGameEvent(it, GameEvent.LobbyUpdated(lobbyDto))
        }
        messenger.sendGameListEvent(GameListEvent.CreateOrUpdate(lobbyDto))
    }

    @MessageMapping("/lobby/addBot")
    fun addBot(@Validated action: AddBotAction, principal: Principal) {
        val lobby = principal.player.ownedLobby
        val bot = runBlocking {
            SevenWondersClient().connectBot("ws://localhost:$serverPort", action.botDisplayName, action.config)
        }
        logger.info("Starting bot {} in game '{}'", action.botDisplayName, lobby.name)
        componentScope.launch {
            val result = withTimeoutOrNull(action.globalBotTimeoutMillis) {
                bot.joinAndAutoPlay(lobby.id)
            }
            if (result == null) {
                meterRegistry.counter("bot.timeout", lobby.playerCountsTags()).increment()
                val timeoutDuration = action.globalBotTimeoutMillis.milliseconds
                logger.error("Bot {} timed out after {}", action.botDisplayName, timeoutDuration)
                bot.disconnect()
            }
        }
    }

    /**
     * Starts the game.
     *
     * @param principal the connected user's information
     */
    @MessageMapping("/lobby/startGame")
    fun startGame(principal: Principal) {
        val lobby = principal.player.ownedLobby
        val game = lobby.startGame()

        meterRegistry.counter("games.started").increment()
        logger.info("Game {} ('{}') successfully started", game.id, lobby.name)

        // we wait for readiness here to ensure all subscriptions are correctly setup on client side
        val currentTurnInfo = game.getCurrentTurnInfo().hideHandsAndWaitForReadiness()
        lobby.resetPlayersReadyState()

        currentTurnInfo.forEach {
            val player = lobby.getPlayers()[it.playerIndex]
            messenger.sendGameEvent(player, GameEvent.GameStarted(it))
        }
        messenger.sendGameListEvent(GameListEvent.CreateOrUpdate(lobby.toDTO()))
    }

    private fun Lobby.resetPlayersReadyState() {
        getPlayers().forEach { it.isReady = false }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(LobbyController::class.java)
    }
}

package org.luxons.sevenwonders.model.api.actions

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import kotlin.time.ExperimentalTime
import kotlin.time.hours

/**
 * The action to choose the player's name. This is the first action that should be called.
 */
@Serializable
class ChooseNameAction(
    /**
     * The display name of the player. May contain spaces and special characters.
     */
    val playerName: String,
    /**
     * The player's icon.
     */
    val icon: Icon?,
    /**
     * Whether the new player is a human (as opposed to a bot).
     */
    val isHuman: Boolean,
)

@Serializable
data class Icon(
    val name: String,
)

/**
 * The action to create a game.
 */
@Serializable
class CreateGameAction(
    /**
     * The name of the game to create.
     */
    val gameName: String,
)

/**
 * The action to join a game.
 */
@Serializable
class JoinGameAction(
    /**
     * The ID of the game to join.
     */
    val gameId: Long,
)

/**
 * The action to prepare the next move during a game.
 */
@Serializable
class PrepareMoveAction(
    /**
     * The move to prepare.
     */
    val move: PlayerMove,
)

/**
 * The action to update the order of the players around the table.
 * Can only be called in the lobby by the owner of the game.
 */
@Serializable
class ReorderPlayersAction(
    /**
     * The list of usernames of the players, in the new order.
     */
    val orderedPlayers: List<String>,
)

/**
 * The action to update the wonders assigned to each player.
 * Can only be called in the lobby by the owner of the game.
 */
@Serializable
class ReassignWondersAction(
    /**
     * The list of wonders assigned to each player, in the players' order.
     */
    val assignedWonders: List<AssignedWonder>,
)

/**
 * The action to update the settings of the game. Can only be called in the lobby by the owner of the game.
 */
@Serializable
class UpdateSettingsAction(
    /**
     * The new values for the settings.
     */
    val settings: Settings,
)

/**
 * The action to add a bot to the game. Can only be called in the lobby by the owner of the game.
 */
@Serializable
@OptIn(ExperimentalTime::class)
class AddBotAction(
    /**
     * The display name for the bot to add.
     */
    val botDisplayName: String,
    /**
     * The global timeout for the bot, after which it disconnects (whether the game is over or not).
     */
    val globalBotTimeoutMillis: Long = 6.hours.toLongMilliseconds(),
    /**
     * The configuration of the bot to add.
     */
    val config: BotConfig = BotConfig(),
)

@Serializable
data class BotConfig(
    val minActionDelayMillis: Long = 50,
    val maxActionDelayMillis: Long = 500,
)

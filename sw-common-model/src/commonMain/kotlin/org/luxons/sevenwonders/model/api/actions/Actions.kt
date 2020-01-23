package org.luxons.sevenwonders.model.api.actions

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.CustomizableSettings
import org.luxons.sevenwonders.model.PlayerMove

/**
 * The action to choose the player's name. This is the first action that should be called.
 */
@Serializable
class ChooseNameAction(
    /**
     * The display name of the player. May contain spaces and special characters.
     */
    val playerName: String
)

/**
 * The action to create a game.
 */
@Serializable
class CreateGameAction(
    /**
     * The name of the game to create.
     */
    val gameName: String
)

/**
 * The action to join a game.
 */
@Serializable
class JoinGameAction(
    /**
     * The ID of the game to join.
     */
    val gameId: Long
)

/**
 * The action to prepare the next move during a game.
 */
@Serializable
class PrepareMoveAction(
    /**
     * The move to prepare.
     */
    val move: PlayerMove
)

/**
 * The action to update the order of the players around the table. Can only be called in the lobby by the owner of the
 * game.
 */
@Serializable
class ReorderPlayersAction(
    /**
     * The list of usernames of the players, in the new order.
     */
    val orderedPlayers: List<String>
)

/**
 * The action to update the settings of the game. Can only be called in the lobby by the owner of the game.
 */
@Serializable
class UpdateSettingsAction(
    /**
     * The new values for the settings.
     */
    val settings: CustomizableSettings
)

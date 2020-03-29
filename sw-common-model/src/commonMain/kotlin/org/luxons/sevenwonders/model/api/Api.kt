package org.luxons.sevenwonders.model.api

import kotlinx.serialization.Serializable

const val SEVEN_WONDERS_WS_ENDPOINT = "/seven-wonders-websocket"

enum class State {
    LOBBY, PLAYING, FINISHED
}

@Serializable
data class LobbyDTO(
    val id: Long,
    val name: String,
    val owner: String,
    val players: List<PlayerDTO>,
    val state: State,
    val hasEnoughPlayers: Boolean,
    val maxPlayersReached: Boolean
) {
    fun joinability(userDisplayName: String): Actionability = when {
        state != State.LOBBY -> Actionability(false, "Cannot join: the game has already started")
        maxPlayersReached -> Actionability(
            false,
            "Cannot join: the game is full"
        )
        playerNameAlreadyUsed(userDisplayName) -> Actionability(
            false,
            "Cannot join: already a player named '$userDisplayName' in this game"
        )
        else -> Actionability(true, "Join game")
    }

    fun startability(username: String): Actionability = when {
        !hasEnoughPlayers -> Actionability(
            false,
            "Cannot start the game, more players needed"
        )
        owner != username -> Actionability(false, "Cannot start the game: only the owner can")
        else -> Actionability(true, "Start game")
    }

    private fun playerNameAlreadyUsed(name: String): Boolean = players.any { it.displayName == name }
}

@Serializable
data class Actionability(
    val canDo: Boolean,
    val tooltip: String
)

@Serializable
data class ConnectedPlayer(
    val username: String,
    val displayName: String
)

@Serializable
data class PlayerDTO(
    val username: String,
    val displayName: String,
    val index: Int,
    val isGameOwner: Boolean,
    val isReady: Boolean
)

package org.luxons.sevenwonders.model.api

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.wonders.PreGameWonder

const val SEVEN_WONDERS_WS_ENDPOINT = "/seven-wonders-websocket"

enum class State {
    LOBBY,
    PLAYING,
    FINISHED,
}

@Serializable
data class LobbyDTO(
    val id: Long,
    val name: String,
    val owner: String,
    val players: List<PlayerDTO>,
    val allWonders: List<PreGameWonder>,
    val state: State,
    val settings: Settings,
    val hasEnoughPlayers: Boolean,
    val maxPlayersReached: Boolean,
) {
    private val wondersByName = allWonders.associateBy { it.name }

    fun findWonder(name: String): PreGameWonder = wondersByName[name] ?: error("Unknown wonder '$name'")

    fun joinability(userDisplayName: String): Actionability = when {
        state == State.PLAYING -> Actionability(false, "Cannot join: the game has already started")
        state == State.FINISHED -> Actionability(false, "Cannot join: the game is over")
        // should only ever happen if a new state is added
        state != State.LOBBY -> Actionability(false, "Cannot join the game at this time")
        maxPlayersReached -> Actionability(false, "Cannot join: the game is full")
        playerNameAlreadyUsed(userDisplayName) -> Actionability(
            canDo = false,
            tooltip = "Cannot join: already a player named '$userDisplayName' in this game",
        )
        else -> Actionability(true, "Join game")
    }

    fun startability(username: String): Actionability = when {
        !hasEnoughPlayers -> Actionability(false, "Cannot start the game, more players needed")
        owner != username -> Actionability(false, "Cannot start the game: only the owner can")
        else -> Actionability(true, "Start game")
    }

    private fun playerNameAlreadyUsed(name: String): Boolean = players.any { it.displayName == name }
}

@Serializable
data class Actionability(
    val canDo: Boolean,
    val tooltip: String,
)

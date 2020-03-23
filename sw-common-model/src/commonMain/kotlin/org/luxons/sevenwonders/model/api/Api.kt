package org.luxons.sevenwonders.model.api

import kotlinx.serialization.Serializable

const val SEVEN_WONDERS_WS_ENDPOINT = "/seven-wonders-websocket"

enum class State {
    LOBBY, PLAYING
}

@Serializable
data class LobbyDTO(
    val id: Long,
    val name: String,
    val owner: String,
    val players: List<PlayerDTO>,
    val state: State,
    val canBeStarted: Boolean
)

@Serializable
data class PlayerDTO(
    val username: String,
    val displayName: String,
    val index: Int,
    val isGameOwner: Boolean,
    val isUser: Boolean
)

package org.luxons.sevenwonders.api

import org.luxons.sevenwonders.lobby.Lobby
import org.luxons.sevenwonders.lobby.State

data class LobbyDTO(
    val id: Long,
    val name: String,
    val owner: String,
    val players: List<PlayerDTO>,
    val state: State
)

fun Lobby.toDTO(currentUser: String): LobbyDTO {
    val players = getPlayers().map { it.toDTO(currentUser) }
    return LobbyDTO(id, name, owner.username, players, state)
}

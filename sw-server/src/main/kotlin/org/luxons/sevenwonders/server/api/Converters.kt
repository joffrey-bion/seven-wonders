package org.luxons.sevenwonders.server.api

import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.server.lobby.Lobby
import org.luxons.sevenwonders.server.lobby.Player

fun Lobby.toDTO(currentUser: String): LobbyDTO {
    val players = getPlayers().map { it.toDTO(currentUser) }
    return LobbyDTO(id, name, owner.username, players, state)
}

fun Player.toDTO(currentUser: String) =
    PlayerDTO(username, displayName, index, isGameOwner, username === currentUser)

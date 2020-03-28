package org.luxons.sevenwonders.server.api

import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.server.lobby.Lobby
import org.luxons.sevenwonders.server.lobby.Player

fun Lobby.toDTO(currentPlayer: Player): LobbyDTO {
    val players = getPlayers().map { it.toDTO(currentPlayer.username) }
    val joinability = joinability(currentPlayer.displayName)
    val startability = startability(currentPlayer.username)
    return LobbyDTO(id, name, owner.username, players, state, joinability, startability)
}

fun Player.toDTO(currentUser: String) =
    PlayerDTO(username, displayName, index, isGameOwner, username === currentUser, isReady)

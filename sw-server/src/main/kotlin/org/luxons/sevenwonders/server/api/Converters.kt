package org.luxons.sevenwonders.server.api

import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.server.lobby.Lobby
import org.luxons.sevenwonders.server.lobby.Player

fun Lobby.toDTO(): LobbyDTO = LobbyDTO(
    id = id,
    name = name,
    owner = owner.username,
    players = getPlayers().map { it.toDTO() },
    state = state,
    hasEnoughPlayers = hasEnoughPlayers(),
    maxPlayersReached = maxPlayersReached()
)

fun Player.toDTO() = PlayerDTO(username, displayName, icon, index, isGameOwner, isReady)

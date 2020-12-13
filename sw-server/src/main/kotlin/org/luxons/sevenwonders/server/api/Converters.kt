package org.luxons.sevenwonders.server.api

import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import org.luxons.sevenwonders.server.lobby.Lobby
import org.luxons.sevenwonders.server.lobby.Player

fun Lobby.toDTO(): LobbyDTO = LobbyDTO(
    id = id,
    name = name,
    owner = owner.username,
    players = getPlayers().zip(getAssignedWonders()).map { (p, w) -> p.toDTO(w) },
    allWonders = allWonders,
    state = state,
    settings = settings,
    hasEnoughPlayers = hasEnoughPlayers(),
    maxPlayersReached = maxPlayersReached(),
)

private fun Player.toDTO(wonder: AssignedWonder) = PlayerDTO(
    username = username,
    displayName = displayName,
    isHuman = isHuman,
    icon = icon,
    wonder = wonder,
    isGameOwner = isGameOwner,
    isReady = isReady,
)

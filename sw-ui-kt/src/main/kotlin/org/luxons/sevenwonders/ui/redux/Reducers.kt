package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import redux.RAction

data class SwState(
    val player: PlayerDTO? = null,
    val lobby: LobbyDTO? = null,
    val games: List<LobbyDTO> = emptyList()
)

fun rootReducer(state: SwState, action: RAction) = when (action) {
    is SetCurrentPlayerAction -> state.copy(player = action.player)
    is UpdateGameListAction -> state.copy(games = action.games)
    is UpdateLobbyAction -> state.copy(lobby = action.lobby)
    is UpdatePlayers -> TODO()
    else -> state
}

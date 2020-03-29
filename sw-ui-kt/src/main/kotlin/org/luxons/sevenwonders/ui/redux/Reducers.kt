package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.State
import redux.RAction

data class SwState(
    val currentPlayer: PlayerDTO? = null,
    // they must be by ID to support updates to a sublist
    val gamesById: Map<Long, LobbyDTO> = emptyMap(),
    val currentPlayerUsername: String? = null,
    val currentLobby: LobbyDTO? = null,
    val currentTurnInfo: PlayerTurnInfo? = null
) {
    val games: List<LobbyDTO> = gamesById.values.toList()
}

fun rootReducer(state: SwState, action: RAction): SwState = state.copy(
    gamesById = gamesReducer(state.gamesById, action),
    currentPlayer = currentPlayerReducer(state.currentPlayer, action),
    currentLobby = currentLobbyReducer(state.currentLobby, action),
    currentTurnInfo = currentTurnInfoReducer(state.currentTurnInfo, action)
)

private fun gamesReducer(games: Map<Long, LobbyDTO>, action: RAction): Map<Long, LobbyDTO> = when (action) {
    is UpdateGameListAction -> (games + action.games.associateBy { it.id }).filterValues { it.state != State.FINISHED }
    else -> games
}

private fun currentPlayerReducer(currentPlayer: PlayerDTO?, action: RAction): PlayerDTO? = when (action) {
    is SetCurrentPlayerAction -> action.player
    else -> currentPlayer
}

private fun currentLobbyReducer(currentLobby: LobbyDTO?, action: RAction): LobbyDTO? = when (action) {
    is EnterLobbyAction -> action.lobby
    is UpdateLobbyAction -> action.lobby
    is PlayerReadyEvent -> currentLobby?.let { l ->
        l.copy(players = l.players.map { p ->
            if (p.username == action.username) p.copy(isReady = true) else p
        })
    }
    else -> currentLobby
}

private fun currentTurnInfoReducer(currentTurnInfo: PlayerTurnInfo?, action: RAction): PlayerTurnInfo? = when (action) {
    is TurnInfoEvent -> action.turnInfo
    else -> currentTurnInfo
}

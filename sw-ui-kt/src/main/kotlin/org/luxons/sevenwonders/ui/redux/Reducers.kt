package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import redux.RAction

data class SwState(
    val currentPlayerUsername: String? = null,
    val currentLobbyId: Long? = null,
    val playersByUsername: Map<String, PlayerDTO> = emptyMap(),
    val gamesById: Map<Long, LobbyDTO> = emptyMap()
) {
    val games: List<LobbyDTO> = gamesById.values.toList()
    val currentLobby: LobbyDTO? = currentLobbyId?.let { gamesById[it] }
    val currentPlayer: PlayerDTO? = currentPlayerUsername?.let { playersByUsername[it] }
}

fun rootReducer(state: SwState, action: RAction): SwState = state.copy(
    currentPlayerUsername = currentPlayerReducer(state.currentPlayerUsername, action),
    currentLobbyId = currentLobbyReducer(state.currentLobbyId, action),
    gamesById = gamesReducer(state.gamesById, action),
    playersByUsername = playersReducer(state.playersByUsername, action)
)

private fun currentPlayerReducer(username: String?, action: RAction): String? = when (action) {
    is SetCurrentPlayerAction -> action.player.username
    else -> username
}

private fun currentLobbyReducer(currentLobby: Long?, action: RAction): Long? = when (action) {
    is EnterLobbyAction -> action.gameId
    else -> currentLobby
}

private fun gamesReducer(games: Map<Long, LobbyDTO>, action: RAction): Map<Long, LobbyDTO> = when (action) {
    is UpdateGameListAction -> action.games.associateBy { it.id } // replaces because should remove deleted games
    is UpdateLobbyAction -> games + (action.lobby.id to action.lobby)
    else -> games
}

private fun playersReducer(playersByUsername: Map<String, PlayerDTO>, action: RAction): Map<String, PlayerDTO> = when (action) {
    is UpdatePlayers -> playersByUsername + action.players
    is UpdateLobbyAction -> playersByUsername + action.lobby.players.associateBy { it.username }
    is SetCurrentPlayerAction -> playersByUsername + (action.player.username to action.player)
    else -> playersByUsername
}

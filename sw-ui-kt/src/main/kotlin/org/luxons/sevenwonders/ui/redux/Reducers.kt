package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.GameState
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import redux.RAction

data class SwState(
    val playersByUsername: Map<String, PlayerDTO> = emptyMap(),
    val gamesById: Map<Long, LobbyDTO> = emptyMap(),
    val currentPlayerUsername: String? = null,
    val currentLobbyId: Long? = null,
    val currentTurnInfo: PlayerTurnInfo? = null,
    val currentTable: GameState? = null
) {
    val games: List<LobbyDTO> = gamesById.values.toList()
    val currentLobby: LobbyDTO? = currentLobbyId?.let { gamesById[it] }
    val currentPlayer: PlayerDTO? = currentPlayerUsername?.let { playersByUsername[it] }
}

fun rootReducer(state: SwState, action: RAction): SwState = state.copy(
    playersByUsername = playersReducer(state.playersByUsername, action),
    gamesById = gamesReducer(state.gamesById, action),
    currentPlayerUsername = currentPlayerReducer(state.currentPlayerUsername, action),
    currentLobbyId = currentLobbyReducer(state.currentLobbyId, action),
    currentTurnInfo = currentTurnInfoReducer(state.currentTurnInfo, action),
    currentTable = currentTableReducer(state.currentTable, action)
)

private fun playersReducer(playersByUsername: Map<String, PlayerDTO>, action: RAction): Map<String, PlayerDTO> = when (action) {
    is UpdatePlayers -> playersByUsername + action.players
    is UpdateLobbyAction -> playersByUsername + action.lobby.players.associateBy { it.username }
    is SetCurrentPlayerAction -> playersByUsername + (action.player.username to action.player)
    else -> playersByUsername
}

private fun gamesReducer(games: Map<Long, LobbyDTO>, action: RAction): Map<Long, LobbyDTO> = when (action) {
    is UpdateGameListAction -> action.games.associateBy { it.id } // replaces because should remove deleted games
    is EnterLobbyAction -> games + (action.lobby.id to action.lobby)
    is UpdateLobbyAction -> games + (action.lobby.id to action.lobby)
    else -> games
}

private fun currentPlayerReducer(username: String?, action: RAction): String? = when (action) {
    is SetCurrentPlayerAction -> action.player.username
    else -> username
}

private fun currentLobbyReducer(currentLobbyId: Long?, action: RAction): Long? = when (action) {
    is EnterLobbyAction -> action.lobby.id
    else -> currentLobbyId
}

private fun currentTurnInfoReducer(currentTurnInfo: PlayerTurnInfo?, action: RAction): PlayerTurnInfo? = when (action) {
    is TurnInfoEvent -> action.turnInfo
    is TableUpdateEvent -> null
    else -> currentTurnInfo
}

private fun currentTableReducer(currentTable: GameState?, action: RAction): GameState? = when (action) {
    is TurnInfoEvent -> action.turnInfo.table
    is TableUpdateEvent -> action.table
    else -> currentTable
}

package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.MoveType
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.GameListEvent
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.cards.CardBack
import org.luxons.sevenwonders.model.cards.HandCard
import org.luxons.sevenwonders.model.resources.ResourceTransactionOptions
import redux.RAction

data class SwState(
    val connectedPlayer: ConnectedPlayer? = null,
    // they must be by ID to support updates to a sublist
    val gamesById: Map<Long, LobbyDTO> = emptyMap(),
    val currentLobby: LobbyDTO? = null,
    val gameState: GameState? = null,
    val fatalError: String? = null,
) {
    val currentPlayer: PlayerDTO? = (gameState?.players ?: currentLobby?.players)?.first {
        it.username == connectedPlayer?.username
    }
    val games: List<LobbyDTO> = gamesById.values.toList()
}

data class GameState(
    val id: Long,
    val players: List<PlayerDTO>,
    val turnInfo: PlayerTurnInfo?,
    val preparedCardsByUsername: Map<String, CardBack?> = emptyMap(),
    val currentPreparedMove: PlayerMove? = null,
    val transactionSelector: TransactionSelectorState? = null,
) {
    val currentPreparedCard: HandCard?
        get() = turnInfo?.hand?.firstOrNull { it.name == currentPreparedMove?.cardName }
}

data class TransactionSelectorState(
    val moveType: MoveType,
    val card: HandCard,
    val transactionsOptions: ResourceTransactionOptions,
)

fun rootReducer(state: SwState, action: RAction): SwState = state.copy(
    gamesById = gamesReducer(state.gamesById, action),
    connectedPlayer = currentPlayerReducer(state.connectedPlayer, action),
    currentLobby = currentLobbyReducer(state.currentLobby, action),
    gameState = gameStateReducer(state.gameState, action),
    fatalError = connectionLostReducer(action),
)

private fun gamesReducer(games: Map<Long, LobbyDTO>, action: RAction): Map<Long, LobbyDTO> = when (action) {
    is UpdateGameListAction -> when (action.event) {
        is GameListEvent.ReplaceList -> action.event.lobbies.associateBy { it.id }
        is GameListEvent.CreateOrUpdate -> games + (action.event.lobby.id to action.event.lobby)
        is GameListEvent.Delete -> games - action.event.lobbyId
    }
    else -> games
}

private fun currentPlayerReducer(currentPlayer: ConnectedPlayer?, action: RAction): ConnectedPlayer? = when (action) {
    is SetCurrentPlayerAction -> action.player
    else -> currentPlayer
}

private fun currentLobbyReducer(currentLobby: LobbyDTO?, action: RAction): LobbyDTO? = when (action) {
    is EnterLobbyAction -> action.lobby
    is LeaveLobbyAction -> null
    is UpdateLobbyAction -> action.lobby
    is PlayerReadyEvent -> currentLobby?.let { l ->
        l.copy(players = l.players.map { p -> if (p.username == action.username) p.copy(isReady = true) else p })
    }
    else -> currentLobby
}

private fun gameStateReducer(gameState: GameState?, action: RAction): GameState? = when (action) {
    is EnterGameAction -> GameState(
        id = action.lobby.id,
        players = action.lobby.players,
        turnInfo = action.turnInfo,
    )
    is PreparedMoveEvent -> gameState?.copy(currentPreparedMove = action.move)
    is RequestUnprepareMove -> gameState?.copy(currentPreparedMove = null)
    is PreparedCardEvent -> gameState?.copy(
        preparedCardsByUsername = gameState.preparedCardsByUsername + (action.card.username to action.card.cardBack),
    )
    is PlayerReadyEvent -> gameState?.copy(
        players = gameState.players.map { p ->
            if (p.username == action.username) p.copy(isReady = true) else p
        },
    )
    is TurnInfoEvent -> gameState?.copy(
        players = gameState.players.map { p -> p.copy(isReady = false) },
        turnInfo = action.turnInfo,
        currentPreparedMove = null,
    )
    is StartTransactionSelection -> gameState?.copy(transactionSelector = action.transactionSelector)
    is CancelTransactionSelection -> gameState?.copy(transactionSelector = null)
    is RequestPrepareMove -> gameState?.copy(transactionSelector = null)
    else -> gameState
}

private fun connectionLostReducer(action: RAction): String? = when (action) {
    is FatalError -> action.message
    else -> null
}

package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.cards.PreparedCard
import redux.RAction

data class SetCurrentPlayerAction(val player: PlayerDTO): RAction

data class UpdateGameListAction(val games: List<LobbyDTO>): RAction

data class UpdateLobbyAction(val lobby: LobbyDTO): RAction

data class EnterLobbyAction(val lobby: LobbyDTO): RAction

data class UpdatePlayers(val players: Map<String, PlayerDTO>): RAction

data class EnterGameAction(val gameId: Long): RAction

data class TurnInfoEvent(val turnInfo: PlayerTurnInfo): RAction

data class PreparedCardEvent(val card: PreparedCard): RAction

data class PlayerReadyEvent(val username: String): RAction

package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.TurnAction
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.events.GameListEvent
import org.luxons.sevenwonders.model.cards.PreparedCard
import redux.RAction

data class FatalError(val message: String) : RAction

data class SetCurrentPlayerAction(val player: ConnectedPlayer) : RAction

data class UpdateGameListAction(val event: GameListEvent) : RAction

data class UpdateLobbyAction(val lobby: LobbyDTO) : RAction

data class EnterLobbyAction(val lobby: LobbyDTO) : RAction

object LeaveLobbyAction : RAction

data class EnterGameAction(val lobby: LobbyDTO, val turnInfo: PlayerTurnInfo<TurnAction.SayReady>) : RAction

data class TurnInfoEvent(val turnInfo: PlayerTurnInfo<*>) : RAction

data class PreparedMoveEvent(val move: PlayerMove) : RAction

data class PreparedCardEvent(val card: PreparedCard) : RAction

data class PlayerReadyEvent(val username: String) : RAction

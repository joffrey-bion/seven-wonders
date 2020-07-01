package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.Settings
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.wonders.AssignedWonder
import redux.RAction

data class RequestChooseName(val playerName: String) : RAction

data class RequestCreateGame(val gameName: String) : RAction

data class RequestJoinGame(val gameId: Long) : RAction

data class RequestAddBot(val botDisplayName: String) : RAction

data class RequestReorderPlayers(val orderedPlayers: List<String>) : RAction

data class RequestReassignWonders(val wonders: List<AssignedWonder>) : RAction

data class RequestUpdateSettings(val settings: Settings) : RAction

class RequestStartGame : RAction

class RequestLeaveLobby : RAction

class RequestLeaveGame : RAction

class RequestSayReady : RAction

data class RequestPrepareMove(val move: PlayerMove) : RAction

class RequestUnprepareMove : RAction

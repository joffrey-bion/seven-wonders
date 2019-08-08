package org.luxons.sevenwonders.ui.redux

import org.luxons.sevenwonders.model.CustomizableSettings
import org.luxons.sevenwonders.model.PlayerMove
import redux.RAction

data class RequestChooseName(val playerName: String): RAction

data class RequestCreateGameAction(val gameName: String): RAction

data class RequestJoinGameAction(val gameId: Long): RAction

data class RequestReorderPlayers(val orderedPlayers: List<String>): RAction

data class RequestUpdateSettings(val settings: CustomizableSettings): RAction

class RequestStartGameAction: RAction

data class PrepareMove(val move: PlayerMove): RAction

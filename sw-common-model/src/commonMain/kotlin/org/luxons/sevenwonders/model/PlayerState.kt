package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.wonders.WonderBuildability

@Serializable
data class PlayerTurnInfo(
    val playerIndex: Int,
    val table: TableState,
    val action: TurnAction,
) {
    val currentAge: Int = table.currentAge
    val wonderBuildability: WonderBuildability = table.boards[playerIndex].wonder.buildability
}

// TODO move to server code
fun Collection<PlayerTurnInfo>.hideHandsAndWaitForReadiness() = map { it.copy(action = TurnAction.SayReady()) }

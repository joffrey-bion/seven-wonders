package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.wonders.WonderBuildability

@Serializable
data class PlayerTurnInfo<out A : TurnAction>(
    val playerIndex: Int,
    val table: TableState,
    val action: A,
) {
    val currentAge: Int = table.currentAge
    val wonderBuildability: WonderBuildability = table.boards[playerIndex].wonder.buildability
}

// TODO move to server code
fun Collection<PlayerTurnInfo<*>>.hideHandsAndWaitForReadiness() = map {
    PlayerTurnInfo(
        playerIndex = it.playerIndex,
        table = it.table,
        action = TurnAction.SayReady,
    )
}

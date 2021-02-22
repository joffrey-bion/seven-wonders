package org.luxons.sevenwonders.model.api.events

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.cards.PreparedCard

// workaround for https://github.com/Kotlin/kotlinx.serialization/issues/1194
@Serializable
data class GameEventWrapper(
    val event: GameEvent
)

fun GameEvent.wrap() = GameEventWrapper(this)

@Serializable
sealed class GameEvent {

    @Serializable
    data class NewTurnStarted(val turnInfo: PlayerTurnInfo) : GameEvent()

    @Serializable
    data class MovePrepared(val move: PlayerMove) : GameEvent()

    @Serializable
    data class CardPrepared(val preparedCard: PreparedCard) : GameEvent()

    @Serializable
    data class PlayerIsReady(val username: String) : GameEvent()
}

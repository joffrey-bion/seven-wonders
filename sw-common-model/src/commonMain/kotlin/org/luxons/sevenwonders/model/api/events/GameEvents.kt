package org.luxons.sevenwonders.model.api.events

import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.PlayerMove
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.TurnAction
import org.luxons.sevenwonders.model.api.ConnectedPlayer
import org.luxons.sevenwonders.model.api.LobbyDTO
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
    data class NameChosen(val player: ConnectedPlayer) : GameEvent()

    @Serializable
    data class LobbyJoined(val lobby: LobbyDTO) : GameEvent()

    @Serializable
    data class LobbyUpdated(val lobby: LobbyDTO) : GameEvent()

    @Serializable
    object LobbyLeft : GameEvent()

    @Serializable
    data class GameStarted(val turnInfo: PlayerTurnInfo<TurnAction.SayReady>) : GameEvent()

    @Serializable
    data class NewTurnStarted(val turnInfo: PlayerTurnInfo<*>) : GameEvent()

    @Serializable
    data class MovePrepared(val move: PlayerMove) : GameEvent()

    @Serializable
    object MoveUnprepared : GameEvent()

    @Serializable
    data class CardPrepared(val preparedCard: PreparedCard) : GameEvent()

    @Serializable
    data class PlayerIsReady(val username: String) : GameEvent()
}

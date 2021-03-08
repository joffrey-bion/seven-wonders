package org.luxons.sevenwonders.model.api.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.luxons.sevenwonders.model.api.LobbyDTO

// workaround for https://github.com/Kotlin/kotlinx.serialization/issues/1194
@Serializable
data class GameListEventWrapper(
    val event: GameListEvent
)

fun GameListEvent.wrap(): GameListEventWrapper = GameListEventWrapper(this)

@Serializable
sealed class GameListEvent {

    @SerialName("ReplaceList")
    @Serializable
    data class ReplaceList(val lobbies: List<LobbyDTO>) : GameListEvent()

    @SerialName("CreateOrUpdate")
    @Serializable
    data class CreateOrUpdate(val lobby: LobbyDTO) : GameListEvent()

    @SerialName("Delete")
    @Serializable
    data class Delete(val lobbyId: Long) : GameListEvent()
}

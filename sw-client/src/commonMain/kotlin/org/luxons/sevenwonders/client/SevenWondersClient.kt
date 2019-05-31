package org.luxons.sevenwonders.client

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.map
import org.hildan.krossbow.client.KrossbowClient
import org.hildan.krossbow.engines.KrossbowSession
import org.hildan.krossbow.engines.KrossbowSubscription
import org.luxons.sevenwonders.model.CustomizableSettings
import org.luxons.sevenwonders.model.GameState
import org.luxons.sevenwonders.model.PlayerTurnInfo
import org.luxons.sevenwonders.model.api.LobbyDTO
import org.luxons.sevenwonders.model.api.PlayerDTO
import org.luxons.sevenwonders.model.api.SEVEN_WONDERS_WS_ENDPOINT
import org.luxons.sevenwonders.model.api.actions.ChooseNameAction
import org.luxons.sevenwonders.model.api.actions.CreateGameAction
import org.luxons.sevenwonders.model.api.actions.JoinGameAction
import org.luxons.sevenwonders.model.api.actions.ReorderPlayersAction
import org.luxons.sevenwonders.model.api.actions.UpdateSettingsAction
import org.luxons.sevenwonders.model.api.errors.ErrorDTO
import org.luxons.sevenwonders.model.cards.PreparedCard

class SevenWondersClient {

    private val stompClient = KrossbowClient()

    suspend fun connect(serverUrl: String): SevenWondersSession {
        val session = stompClient.connect("$serverUrl$SEVEN_WONDERS_WS_ENDPOINT")
        return SevenWondersSession(session)
    }
}

suspend inline fun <reified T : Any> KrossbowSession.request(
    sendDestination: String,
    receiveDestination: String,
    payload: Any? = null
): T {
    val sub = subscribe<T>(receiveDestination)
    send(sendDestination, payload)
    val msg = sub.messages.receive()
    sub.unsubscribe()
    return msg.payload
}

data class SevenWondersSubscription<T : Any>(
    val messages: ReceiveChannel<T>,
    val unsubscribe: suspend () -> Unit
)

private fun <T : Any> KrossbowSubscription<T>.ignoreHeaders() = SevenWondersSubscription(
    messages = messages.map { it.payload },
    unsubscribe = { unsubscribe() }
)

class SevenWondersSession(private val stompSession: KrossbowSession) {

    suspend fun disconnect() = stompSession.disconnect()

    suspend fun watchErrors(): KrossbowSubscription<ErrorDTO> =
        stompSession.subscribe("/user/queue/errors")

    suspend fun chooseName(displayName: String): PlayerDTO {
        val action = ChooseNameAction(displayName)
        return stompSession.request("/app/chooseName", "/user/queue/nameChoice", action)
    }

    suspend fun watchGames(): SevenWondersSubscription<Array<LobbyDTO>> =
        stompSession.subscribe<Array<LobbyDTO>>("/topic/games").ignoreHeaders()

    suspend fun createGame(gameName: String): LobbyDTO {
        val action = CreateGameAction(gameName)
        return stompSession.request("/app/lobby/create", "/user/queue/lobby/joined", action)
    }

    suspend fun joinGame(gameId: Long): LobbyDTO {
        val action = JoinGameAction(gameId)
        return stompSession.request("/app/lobby/join", "/user/queue/lobby/joined", action)
    }

    suspend fun leaveGame() {
        stompSession.send("/app/lobby/leave")
    }

    suspend fun reorderPlayers(players: List<String>) {
        stompSession.send("/app/lobby/reorderPlayers", ReorderPlayersAction(players))
    }

    suspend fun updateSettings(settings: CustomizableSettings) {
        stompSession.send("/app/lobby/reorderPlayers", UpdateSettingsAction(settings))
    }

    suspend fun watchLobbyUpdates(gameId: Long): SevenWondersSubscription<LobbyDTO> =
        stompSession.subscribe<LobbyDTO>("/topic/lobby/$gameId/updated").ignoreHeaders()

    suspend fun watchGameStart(gameId: Long): SevenWondersSubscription<Unit> =
        stompSession.subscribe<Unit>("/topic/lobby/$gameId/started").ignoreHeaders()

    suspend fun startGame(gameId: Long) {
        val sendDestination = "/app/lobby/startGame"
        val receiveDestination = "/topic/lobby/$gameId/started"
        stompSession.request<Unit>(sendDestination, receiveDestination)
    }

    suspend fun watchPlayerReady(gameId: Long): SevenWondersSubscription<String> =
            stompSession.subscribe<String>("/topic/game/$gameId/playerReady").ignoreHeaders()

    suspend fun watchTableUpdates(gameId: Long): SevenWondersSubscription<GameState> =
            stompSession.subscribe<GameState>("/topic/game/$gameId/tableUpdates").ignoreHeaders()

    suspend fun watchPreparedCards(gameId: Long): SevenWondersSubscription<PreparedCard> =
            stompSession.subscribe<PreparedCard>("/topic/game/$gameId/prepared").ignoreHeaders()

    suspend fun watchTurns(): SevenWondersSubscription<PlayerTurnInfo> =
            stompSession.subscribe<PlayerTurnInfo>("/user/queue/game/turn").ignoreHeaders()

    suspend fun sayReady() {
        stompSession.send("/app/game/sayReady")
    }
}

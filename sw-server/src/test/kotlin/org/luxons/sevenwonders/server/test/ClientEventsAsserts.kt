package org.luxons.sevenwonders.server.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.withTimeoutOrNull
import org.luxons.sevenwonders.client.SevenWondersSession
import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.model.api.events.GameListEvent
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class EventAsserter(
    val gameListEvents: ReceiveChannel<GameListEvent>,
    val gameEvents: ReceiveChannel<GameEvent>,
)

@OptIn(FlowPreview::class)
suspend fun SevenWondersSession.eventAsserter(scope: CoroutineScope): EventAsserter {
    val gameListEvents = watchGames().produceIn(scope)
    val gameEvents = watchGameEvents().produceIn(scope)
    return EventAsserter(gameListEvents, gameEvents)
}

suspend inline fun EventAsserter.expectNoGameEvent(message: String? = null, timeout: Duration = 50.milliseconds) {
    val event = withTimeoutOrNull(timeout) { gameEvents.receive() }
    val extraMessage = message?.let { " ($it)" } ?: ""
    assertNull(event, "Expected no game event$extraMessage, but received $event")
}

suspend inline fun <reified T : GameEvent> EventAsserter.expectGameEvent(timeout: Duration = 1.seconds): T {
    val event = withTimeoutOrNull(timeout) { gameEvents.receive() }
    assertNotNull(event, "Expected event of type ${T::class.simpleName}, received nothing in $timeout")
    assertTrue(event is T, "Expected event of type ${T::class.simpleName}, received $event")
    return event
}

suspend inline fun <reified T : GameListEvent> EventAsserter.expectGameListEvent(timeout: Duration = 1.seconds): T {
    val event = withTimeoutOrNull(timeout) { gameListEvents.receive() }
    assertNotNull(event, "Expected event of type ${T::class.simpleName}, received nothing in $timeout")
    assertTrue(event is T, "Expected event of type ${T::class.simpleName}, received $event")
    return event
}

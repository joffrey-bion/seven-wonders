package org.luxons.sevenwonders.server.test

import org.luxons.sevenwonders.model.api.events.GameEvent
import org.luxons.sevenwonders.model.api.events.GameEventWrapper
import org.luxons.sevenwonders.model.api.events.GameListEvent
import org.luxons.sevenwonders.model.api.events.GameListEventWrapper
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MockMessageChannel : MessageChannel {
    private val _messages: MutableList<Message<*>> = mutableListOf()
    val messages: List<Message<*>> get() = _messages

    override fun send(message: Message<*>, timeout: Long): Boolean = _messages.add(message)

    fun expectSentMessageTo(expectedDestination: String): Message<*> {
        val m = _messages.removeFirstOrNull()
        assertNotNull(m, "Expected sent message, but no messages found")
        assertEquals(expectedDestination, m.headers["simpDestination"], "Incorrect message destination")
        return m
    }

    fun expectNoMoreMessages() {
        assertTrue(
            actual = messages.isEmpty(),
            message = "No more messages should have been sent, but ${messages.size} were found: $messages",
        )
    }
}

inline fun <reified T> MockMessageChannel.expectSentMessageWithPayload(expectedDestination: String): T {
    val m = expectSentMessageTo(expectedDestination)
    val payload = m.payload
    assertTrue(payload is T, "Message payload should be of type ${T::class.simpleName}")
    return payload
}

inline fun <reified T : GameListEvent> MockMessageChannel.expectSentGameListEvent(): T {
    val wrappedEvent = expectSentMessageWithPayload<GameListEventWrapper>("/topic/games")
    val event = wrappedEvent.event
    assertTrue(event is T, "Expected game list event of type ${T::class.simpleName}, got $event")
    return event
}

inline fun <reified T : GameEvent> MockMessageChannel.expectSentGameEventTo(username: String): T {
    val wrappedEvent = expectSentMessageWithPayload<GameEventWrapper>("/user/$username/queue/game/events")
    val event = wrappedEvent.event
    assertTrue(event is T, "Expected game event of type ${T::class.simpleName}, got $event")
    return event
}

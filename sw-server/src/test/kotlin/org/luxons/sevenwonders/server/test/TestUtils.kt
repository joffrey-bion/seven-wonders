package org.luxons.sevenwonders.server.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.SimpMessagingTemplate
import kotlin.test.assertNotNull

fun mockSimpMessagingTemplate(): SimpMessagingTemplate = SimpMessagingTemplate(object : MessageChannel {
    override fun send(message: Message<*>): Boolean = true
    override fun send(message: Message<*>, timeout: Long): Boolean = true
})

fun runAsyncTest(timeoutMillis: Long = 5000, block: suspend CoroutineScope.() -> Unit) = runBlocking {
    val result = withTimeoutOrNull(timeoutMillis, block)
    assertNotNull(result, "Test timed out, exceeded ${timeoutMillis}ms")
}

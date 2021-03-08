package org.luxons.sevenwonders.server.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.test.assertNotNull

fun runAsyncTest(timeoutMillis: Long = 10000, block: suspend CoroutineScope.() -> Unit) = runBlocking {
    val result = withTimeoutOrNull(timeoutMillis, block)
    assertNotNull(result, "Test timed out, exceeded ${timeoutMillis}ms")
}

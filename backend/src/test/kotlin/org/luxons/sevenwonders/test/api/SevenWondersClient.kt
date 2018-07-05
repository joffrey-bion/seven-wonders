package org.luxons.sevenwonders.test.api

import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

import org.hildan.jackstomp.JackstompClient
import org.hildan.jackstomp.JackstompSession

class SevenWondersClient @JvmOverloads constructor(private val client: JackstompClient = JackstompClient()) {

    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    fun connect(serverUrl: String): SevenWondersSession {
        val session = client.connect(serverUrl + WEBSOCKET_ENDPOINT)
        return SevenWondersSession(session)
    }

    fun stop() {
        client.stop()
    }

    companion object {

        private val WEBSOCKET_ENDPOINT = "/seven-wonders-websocket"
    }
}

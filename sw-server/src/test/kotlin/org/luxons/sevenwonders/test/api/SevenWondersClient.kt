package org.luxons.sevenwonders.test.api

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.hildan.jackstomp.JackstompClient
import org.luxons.sevenwonders.config.SEVEN_WONDERS_WS_ENDPOINT
import org.luxons.sevenwonders.engine.resources.MutableResources
import org.luxons.sevenwonders.engine.resources.Resources
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException

class SevenWondersClient {

    private val client: JackstompClient

    init {
        val customMappingsModule = SimpleModule("ConcreteResourcesDeserializationModule")
        customMappingsModule.addAbstractTypeMapping(Resources::class.java, MutableResources::class.java)

        val mappingJackson2MessageConverter = MappingJackson2MessageConverter()
        mappingJackson2MessageConverter.objectMapper.registerModule(customMappingsModule)
        mappingJackson2MessageConverter.objectMapper.registerModule(KotlinModule())

        client = JackstompClient()
        client.webSocketClient.messageConverter = mappingJackson2MessageConverter
    }

    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    fun connect(serverUrl: String): SevenWondersSession {
        val session = client.syncConnect(serverUrl + SEVEN_WONDERS_WS_ENDPOINT)
        return SevenWondersSession(session)
    }

    fun stop() {
        client.stop()
    }
}

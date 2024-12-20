package org.luxons.sevenwonders.server.config

import org.luxons.sevenwonders.model.api.SEVEN_WONDERS_WS_ENDPOINT
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.KotlinSerializationJsonMessageConverter
import org.springframework.messaging.converter.MessageConverter
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val topicSubscriptionInterceptor: TopicSubscriptionInterceptor,
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // prefixes for all subscriptions
        config.enableSimpleBroker("/queue", "/topic").apply {
            setTaskScheduler(createTaskScheduler()) // to support heart beats
        }
        config.setUserDestinationPrefix("/user")

        // /app for normal calls, /topic for subscription events
        config.setApplicationDestinationPrefixes("/app", "/topic")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint(SEVEN_WONDERS_WS_ENDPOINT)
            .setHandshakeHandler(handshakeHandler())
            .setAllowedOrigins("*") // to allow any client to use the API
    }

    @Bean
    fun handshakeHandler(): DefaultHandshakeHandler = AnonymousUsersHandshakeHandler()

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(topicSubscriptionInterceptor)
    }

    override fun configureMessageConverters(messageConverters: MutableList<MessageConverter>): Boolean {
        messageConverters.clear() // remove jackson and other undesired defaults
        messageConverters.add(KotlinSerializationJsonMessageConverter())
        return true
    }
}

private fun createTaskScheduler() = ThreadPoolTaskScheduler().apply {
    poolSize = 1
    threadNamePrefix = "stomp-heartbeat-thread-"
    initialize()
}

package org.luxons.sevenwonders.server.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

const val SEVEN_WONDERS_WS_ENDPOINT = "/seven-wonders-websocket"

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig @Autowired constructor(private val topicSubscriptionInterceptor: TopicSubscriptionInterceptor) :
    WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        // prefixes for all subscriptions
        config.enableSimpleBroker("/queue", "/topic")
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
    fun handshakeHandler(): DefaultHandshakeHandler {
        return AnonymousUsersHandshakeHandler()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(topicSubscriptionInterceptor)
    }
}

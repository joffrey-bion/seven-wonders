package org.luxons.sevenwonders.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.springframework.web.socket.server.support.DefaultHandshakeHandler

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
        registry.addEndpoint("/seven-wonders-websocket")
            .setHandshakeHandler(handshakeHandler())
            .setAllowedOrigins("http://localhost:3000") // to allow frontend server proxy requests in dev mode
            .withSockJS()
    }

    @Bean
    fun handshakeHandler(): DefaultHandshakeHandler {
        return AnonymousUsersHandshakeHandler()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(topicSubscriptionInterceptor)
    }
}

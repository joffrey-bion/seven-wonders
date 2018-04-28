package org.luxons.sevenwonders.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TopicSubscriptionInterceptor topicSubscriptionInterceptor;

    @Autowired
    public WebSocketConfig(TopicSubscriptionInterceptor topicSubscriptionInterceptor) {
        this.topicSubscriptionInterceptor = topicSubscriptionInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // prefixes for all subscriptions
        config.enableSimpleBroker("/queue", "/topic");
        config.setUserDestinationPrefix("/user");

        // /app for normal calls, /topic for subscription events
        config.setApplicationDestinationPrefixes("/app", "/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/seven-wonders-websocket")
                .setHandshakeHandler(handshakeHandler())
                .setAllowedOrigins("http://localhost:3000") // to allow frontend server proxy requests in dev mode
                .withSockJS();
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new AnonymousUsersHandshakeHandler();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(topicSubscriptionInterceptor);
    }
}

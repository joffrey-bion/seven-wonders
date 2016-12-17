package org.luxons.sevenwonders.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // prefixes for all subscriptions
        config.enableSimpleBroker("/queue", "/topic");
        config.setUserDestinationPrefix("/user");

        // prefix for all calls from clients
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/seven-wonders-websocket").setHandshakeHandler(handshakeHandler()).withSockJS();
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new AnonymousUsersHandshakeHandler();
    }

}
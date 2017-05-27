package org.luxons.sevenwonders.config;

import org.luxons.sevenwonders.validation.DestinationAccessValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

@Component
public class TopicSubscriptionInterceptor extends ChannelInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TopicSubscriptionInterceptor.class);

    private final DestinationAccessValidator destinationAccessValidator;

    @Autowired
    public TopicSubscriptionInterceptor(DestinationAccessValidator destinationAccessValidator) {
        this.destinationAccessValidator = destinationAccessValidator;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(headerAccessor.getCommand())) {
            String username = headerAccessor.getUser().getName();
            String destination = headerAccessor.getDestination();
            if (!destinationAccessValidator.hasAccess(username, destination)) {
                sendForbiddenSubscriptionError(username, destination);
                return null;
            }
        }
        return message;
    }

    private void sendForbiddenSubscriptionError(String username, String destination) {
        logger.error(String.format("Player '%s' is not allowed to access %s", username, destination));
    }
}

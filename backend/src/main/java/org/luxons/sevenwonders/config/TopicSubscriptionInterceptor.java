package org.luxons.sevenwonders.config;

import org.luxons.sevenwonders.errors.ApiMisuseException;
import org.luxons.sevenwonders.validation.DestinationAccessValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;

@Component
public class TopicSubscriptionInterceptor extends ChannelInterceptorAdapter {

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
                throw new ForbiddenSubscriptionException(username, destination);
            }
        }
        return message;
    }

    private static class ForbiddenSubscriptionException extends ApiMisuseException {

        ForbiddenSubscriptionException(String username, String destination) {
            super(String.format("Player '%s' is not allowed to access %s", username, destination));
        }
    }
}

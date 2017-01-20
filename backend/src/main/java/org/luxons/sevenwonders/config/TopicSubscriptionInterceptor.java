package org.luxons.sevenwonders.config;

import java.security.Principal;

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
            Principal userPrincipal = headerAccessor.getUser();
            if (!destinationAccessValidator.hasAccess(userPrincipal.getName(), headerAccessor.getDestination())) {
                throw new ForbiddenSubscriptionException();
            }
        }
        return message;
    }

    private static class ForbiddenSubscriptionException extends RuntimeException {
    }
}

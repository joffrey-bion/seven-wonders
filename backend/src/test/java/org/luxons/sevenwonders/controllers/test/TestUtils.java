package org.luxons.sevenwonders.controllers.test;

import java.security.Principal;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class TestUtils {

    public static Principal createPrincipal(String username) {
        return new Principal() {
            @Override
            public String getName() {
                return username;
            }
        };
    }

    public static SimpMessagingTemplate createSimpMessagingTemplate() {
        MessageChannel messageChannel = new MessageChannel() {
            @Override
            public boolean send(Message<?> message) {
                return true;
            }

            @Override
            public boolean send(Message<?> message, long timeout) {
                return true;
            }
        };
        return new SimpMessagingTemplate(messageChannel);
    }
}

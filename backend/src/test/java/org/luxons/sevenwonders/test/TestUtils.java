package org.luxons.sevenwonders.test;

import java.security.Principal;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class TestUtils {

    public static Principal createPrincipal(String username) {
        // the Principal interface just contains a getName() method
        return () -> username;
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

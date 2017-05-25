package org.luxons.sevenwonders.test;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class TestUtils {

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

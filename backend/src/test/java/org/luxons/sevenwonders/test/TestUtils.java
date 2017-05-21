package org.luxons.sevenwonders.test;

import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class TestUtils {

    private static final Logger logger = LoggerFactory.getLogger(TestUtils.class);

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

    public static WebSocketStompClient createStompClient() {
        WebSocketStompClient stompClient = new WebSocketStompClient(createWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter()); // for custom object exchanges
        stompClient.setTaskScheduler(createTaskScheduler()); // for heartbeats
        return stompClient;
    }

    private static WebSocketClient createWebSocketClient() {
        return new SockJsClient(createWsTransports());
    }

    private static List<Transport> createWsTransports() {
        return Collections.singletonList(new WebSocketTransport(new StandardWebSocketClient()));
    }

    private static ThreadPoolTaskScheduler createTaskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.afterPropertiesSet();
        return taskScheduler;
    }

    public static StompSession connect(WebSocketStompClient stompClient, String url) throws InterruptedException,
            ExecutionException, TimeoutException {
        StompSession session = stompClient.connect(url, new TestStompSessionHandler()).get(5, TimeUnit.SECONDS);        session.setAutoReceipt(true);
        return session;
    }

    private static class TestStompSessionHandler extends StompSessionHandlerAdapter {

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            logger.info("Connected under session id " + session.getSessionId());
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            logger.debug("Frame received");
        }

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return String.class;
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers,
                                    byte[] payload, Throwable exception) {
            logger.error("Exception thrown", exception);
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            logger.error("Transport exception thrown", exception);
        }
    }
}

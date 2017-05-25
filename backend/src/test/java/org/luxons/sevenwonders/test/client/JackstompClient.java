package org.luxons.sevenwonders.test.client;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class JackstompClient {

    private static final long DEFAULT_CONNECTION_TIMEOUT_IN_SECONDS = 15;

    private final WebSocketStompClient client;

    public JackstompClient() {
        this(createDefaultStompClient());
    }

    public JackstompClient(WebSocketStompClient client) {
        this.client = client;
    }

    private static WebSocketStompClient createDefaultStompClient() {
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

    public JackstompSession connect(String url) throws InterruptedException, ExecutionException, TimeoutException {
        return connect(url, DEFAULT_CONNECTION_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    public JackstompSession connect(String url, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        StompSession session = client.connect(url, new LoggingStompSessionHandler()).get(timeout, unit);
        session.setAutoReceipt(true);
        return new JackstompSession(session);
    }

    public void stop() {
        client.stop();
    }
}

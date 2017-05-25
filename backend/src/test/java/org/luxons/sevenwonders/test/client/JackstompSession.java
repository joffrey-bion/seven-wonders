package org.luxons.sevenwonders.test.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

public class JackstompSession implements StompSession {

    private final StompSession stompSession;

    public JackstompSession(StompSession stompSession) {
        this.stompSession = stompSession;
    }

    @Override
    public Receiptable send(String destination, Object payload) {
        return stompSession.send(destination, payload);
    }

    @Override
    public Receiptable send(StompHeaders headers, Object payload) {
        return stompSession.send(headers, payload);
    }

    @Override
    public Subscription subscribe(String destination, StompFrameHandler handler) {
        return stompSession.subscribe(destination, handler);
    }

    @Override
    public Subscription subscribe(StompHeaders headers, StompFrameHandler handler) {
        return stompSession.subscribe(headers, handler);
    }

    public <T> Channel<T> subscribe(String destination, Class<T> payloadType) {
        BlockingQueue<T> blockingQueue = new LinkedBlockingDeque<>();
        StompFrameHandler frameHandler = new QueuedStompFrameHandler<>(blockingQueue, payloadType);
        Subscription sub = stompSession.subscribe(destination, frameHandler);
        return new Channel<>(sub, blockingQueue);
    }

    public Channel<Object> subscribeEmptyMsgs(String destination) {
        BlockingQueue<Object> blockingQueue = new LinkedBlockingDeque<>();
        StompFrameHandler frameHandler = new EmptyMsgStompFrameHandler(blockingQueue);
        Subscription sub = stompSession.subscribe(destination, frameHandler);
        return new Channel<>(sub, blockingQueue);
    }
    @Override
    public Receiptable acknowledge(String messageId, boolean consumed) {
        return stompSession.acknowledge(messageId, consumed);
    }

    @Override
    public String getSessionId() {
        return stompSession.getSessionId();
    }

    @Override
    public boolean isConnected() {
        return stompSession.isConnected();
    }

    @Override
    public void setAutoReceipt(boolean enabled) {
        stompSession.setAutoReceipt(enabled);
    }

    @Override
    public void disconnect() {
        stompSession.disconnect();
    }

    public <T> T request(Object payload, Class<T> responseType, String requestDestination, String responseDestination)
            throws InterruptedException {
        Channel<T> channel = subscribe(responseDestination, responseType);
        send(requestDestination, payload);
        T msg = channel.next();
        channel.unsubscribe();
        return msg;
    }

    public boolean request(String requestDestination, Object payload, String responseDestination)
            throws InterruptedException {
        Channel<Object> channel = subscribeEmptyMsgs(responseDestination);
        send(requestDestination, payload);
        Object msg = channel.next();
        channel.unsubscribe();
        return msg != null;
    }
}

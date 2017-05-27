package org.luxons.sevenwonders.test.client;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

/**
 * Wrapper a {@link StompSession} that provides additional subscription features. It can return a {@link Channel}
 * upon subscription for a given payload type, which can then be queried actively. This is particularly useful for
 * unit tests.
 */
public class JackstompSession implements StompSession {

    private final StompSession stompSession;

    /**
     * Creates a new {@code JackstompSession} wrapping the given {@link StompSession}.
     *
     * @param stompSession
     *         the session to wrap
     */
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

    /**
     * Subscribe to the given destination by sending a SUBSCRIBE frame and queue received messages in the returned
     * {@link Channel}.
     *
     * @param destination
     *         the destination to subscribe to
     * @param payloadType
     *         the expected type for received messages
     *
     * @return a new channel to use to actively check for received values, unsubscribe, or track receipts
     */
    public <T> Channel<T> subscribe(String destination, Class<T> payloadType) {
        BlockingQueue<T> blockingQueue = new LinkedBlockingDeque<>();
        StompFrameHandler frameHandler = new QueuedStompFrameHandler<>(blockingQueue, payloadType);
        Subscription sub = stompSession.subscribe(destination, frameHandler);
        return new Channel<>(sub, blockingQueue);
    }

    /**
     * Subscribe to the given destination by sending a SUBSCRIBE frame and queue received messages in the returned
     * {@link Channel}. The messages are expected to have no body, and empty {@link Object}s are queued to be able to
     * track reception events.
     *
     * @param destination
     *         the destination to subscribe to
     *
     * @return a new channel to use to actively check for received events, unsubscribe, or track receipts
     */
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

    /**
     * Makes a synchronous request/response call via a send and a subscription.
     *
     * @param payload
     *         the payload to send on the given requestDestination
     * @param responseType
     *         the type of the response to expect on the responseDestination
     * @param requestDestination
     *         the destination to send payload to
     * @param responseDestination
     *         the destination to expect a response on
     * @param <T>
     *         the type of object to receive as a response
     *
     * @return the response object, deserialized from the JSON received on the responseDestination, or null if no
     * response was received before timeout.
     * @throws InterruptedException
     *         if the current thread was interrupted while waiting for the response
     */
    public <T> T request(Object payload, Class<T> responseType, String requestDestination, String responseDestination)
            throws InterruptedException {
        Channel<T> channel = subscribe(responseDestination, responseType);
        send(requestDestination, payload);
        T msg = channel.next();
        channel.unsubscribe();
        return msg;
    }

    /**
     * Makes a synchronous request/response call via a send and a subscription, but does not expect any value as
     * response, just an event message with no body.
     *
     * @param payload
     *         the payload to send on the given requestDestination
     * @param requestDestination
     *         the destination to send payload to
     * @param responseDestination
     *         the destination to expect a response on
     *
     * @return true if the event message was received before timeout, false otherwise
     * @throws InterruptedException
     *         if the current thread was interrupted while waiting for the response
     */
    public boolean request(Object payload, String requestDestination, String responseDestination)
            throws InterruptedException {
        Channel<Object> channel = subscribeEmptyMsgs(responseDestination);
        send(requestDestination, payload);
        Object msg = channel.next();
        channel.unsubscribe();
        return msg != null;
    }
}

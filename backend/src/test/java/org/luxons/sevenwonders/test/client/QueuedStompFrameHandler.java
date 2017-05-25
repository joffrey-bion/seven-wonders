package org.luxons.sevenwonders.test.client;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

class QueuedStompFrameHandler<T> implements StompFrameHandler {

    private final BlockingQueue<T> blockingQueue;

    private final Class<T> type;

    QueuedStompFrameHandler(BlockingQueue<T> blockingQueue, Class<T> type) {
        this.blockingQueue = blockingQueue;
        this.type = type;
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return type;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        if (o == null) {
            throw new IllegalArgumentException("Unsupported null payloads in this type of frame handler");
        }
        blockingQueue.offer(type.cast(o));
    }
}

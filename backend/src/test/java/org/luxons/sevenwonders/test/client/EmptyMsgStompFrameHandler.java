package org.luxons.sevenwonders.test.client;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

class EmptyMsgStompFrameHandler implements StompFrameHandler {

    private final BlockingQueue<Object> blockingQueue;

    EmptyMsgStompFrameHandler(BlockingQueue<Object> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return Object.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        if (o != null) {
            throw new IllegalArgumentException("Non-null payload in EmptyMsgStompFrameHandler");
        }
        blockingQueue.offer(new Object());
    }
}

package org.luxons.sevenwonders.test;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

public class TestStompFrameHandler<T> implements StompFrameHandler {

    private static final Logger logger = LoggerFactory.getLogger(TestStompFrameHandler.class);

    private final BlockingQueue<T> blockingQueue;

    private final Class<T> type;

    public static TestStompFrameHandler<String> defaultHandler(BlockingQueue<String> blockingQueue) {
        return new TestStompFrameHandler<>(blockingQueue, String.class);
    }

    public TestStompFrameHandler(BlockingQueue<T> blockingQueue, Class<T> type) {
        this.blockingQueue = blockingQueue;
        this.type = type;
    }

    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        logger.info("Handler.getPayloadType, headers = " + stompHeaders);
//        throw new RuntimeException("TEST EXCEPTION");
        return type;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object o) {
        blockingQueue.offer(type.cast(o));
//        try {
//            blockingQueue.offer(new ObjectMapper().readValue((String) o, type));
//        } catch (IOException e) {
//            throw new RuntimeException("Could not convert frame", e);
//        }
    }
}

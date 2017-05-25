package org.luxons.sevenwonders.test.client;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

class LoggingStompSessionHandler extends StompSessionHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingStompSessionHandler.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("Client connected under session id " + session.getSessionId());
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return String.class;
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                                Throwable exception) {
        logger.error("Exception thrown in session " + session.getSessionId(), exception);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        logger.error("Transport exception thrown in session " + session.getSessionId(), exception);
    }
}

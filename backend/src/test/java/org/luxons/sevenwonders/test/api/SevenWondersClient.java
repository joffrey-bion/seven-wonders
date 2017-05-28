package org.luxons.sevenwonders.test.api;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.hildan.jackstomp.JackstompClient;
import org.hildan.jackstomp.JackstompSession;

public class SevenWondersClient {

    private static final String WEBSOCKET_ENDPOINT = "/seven-wonders-websocket";

    private final JackstompClient client;

    public SevenWondersClient() {
        this(new JackstompClient());
    }

    public SevenWondersClient(JackstompClient client) {
        this.client = client;
    }

    public SevenWondersSession connect(String serverUrl)
            throws InterruptedException, ExecutionException, TimeoutException {
        JackstompSession session = client.connect(serverUrl + WEBSOCKET_ENDPOINT);
        return new SevenWondersSession(session);
    }

    public void stop() {
        client.stop();
    }
}

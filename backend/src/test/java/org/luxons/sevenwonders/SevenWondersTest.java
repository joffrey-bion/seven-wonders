package org.luxons.sevenwonders;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.actions.ChooseNameAction;
import org.luxons.sevenwonders.test.ClientPlayer;
import org.luxons.sevenwonders.test.TestStompFrameHandler;
import org.luxons.sevenwonders.test.TestUtils;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SevenWondersTest {

    private static final String WEBSOCKET_URI = "ws://localhost:%d/seven-wonders-websocket";

    private static WebSocketStompClient stompClient;

    @LocalServerPort
    private int randomServerPort;

    @BeforeClass
    public static void setUp() {
        stompClient = TestUtils.createStompClient();
    }

    @Test
    public void testConnection() throws Exception {
        StompSession session = TestUtils.connect(stompClient, String.format(WEBSOCKET_URI, randomServerPort));
        BlockingQueue<ClientPlayer> blockingQueue = new LinkedBlockingDeque<>();
        Subscription sub = session.subscribe("/user/queue/nameChoice",
                new TestStompFrameHandler<>(blockingQueue, ClientPlayer.class));

        String testName = "Test User";
        ChooseNameAction chooseNameAction = new ChooseNameAction();
        chooseNameAction.setPlayerName(testName);

        session.send("/app/chooseName", chooseNameAction);

        ClientPlayer player = blockingQueue.poll(2, SECONDS);
        assertNotNull(player);
        assertEquals(testName, player.getDisplayName());

        sub.unsubscribe();
        session.disconnect();
    }

    @AfterClass
    public static void tearDown() {
        stompClient.stop();
    }
}

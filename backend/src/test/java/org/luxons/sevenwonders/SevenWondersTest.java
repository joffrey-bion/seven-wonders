package org.luxons.sevenwonders;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.test.api.ApiLobby;
import org.luxons.sevenwonders.test.api.SevenWondersSession;
import org.luxons.sevenwonders.test.client.JsonStompClient;
import org.luxons.sevenwonders.test.client.JsonStompSession;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SevenWondersTest {

    private static final String WEBSOCKET_URI = "ws://localhost:%d/seven-wonders-websocket";

    private static JsonStompClient client;

    @LocalServerPort
    private int randomServerPort;

    @BeforeClass
    public static void setUp() {
        client = new JsonStompClient();
    }

    private SevenWondersSession connectNewUser() throws Exception {
        JsonStompSession session = client.connect(String.format(WEBSOCKET_URI, randomServerPort));
        return new SevenWondersSession(session);
    }

    private void disconnect(SevenWondersSession... sessions) {
        for (SevenWondersSession session : sessions) {
            session.disconnect();
        }
    }

    @Test
    public void testConnection() throws Exception {
        SevenWondersSession session = connectNewUser();
        session.chooseName("Test User");
        session.disconnect();
    }

    @Test
    public void testConnection_2users() throws Exception {
        SevenWondersSession session1 = connectNewUser();
        SevenWondersSession session2 = connectNewUser();
        session1.chooseName("Player1");
        session2.chooseName( "Player2");

        ApiLobby lobby = session1.createGame("Test Game");
        session2.joinGame(lobby.getId());

        SevenWondersSession session3 = connectNewUser();
        session3.chooseName("Player3");
        session3.joinGame(lobby.getId());

        session1.startGame(lobby.getId());

        disconnect(session1, session2, session3);
    }

    @AfterClass
    public static void tearDown() {
        client.stop();
    }
}

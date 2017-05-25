package org.luxons.sevenwonders;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.test.api.ApiLobby;
import org.luxons.sevenwonders.test.api.SevenWondersClient;
import org.luxons.sevenwonders.test.api.SevenWondersSession;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SevenWondersTest {

    private static final String WEBSOCKET_URI = "ws://localhost:%d";

    private static SevenWondersClient client;

    @LocalServerPort
    private int randomServerPort;

    private String serverUrl;

    @BeforeClass
    public static void createClient() {
        client = new SevenWondersClient();
    }

    @Before
    public void setServerUrl() {
        serverUrl = String.format(WEBSOCKET_URI, randomServerPort);
    }

    private void disconnect(SevenWondersSession... sessions) {
        for (SevenWondersSession session : sessions) {
            session.disconnect();
        }
    }

    @Test
    public void testConnection() throws Exception {
        SevenWondersSession session = client.connect(serverUrl);
        session.chooseName("Test User");
        session.disconnect();
    }

    @Test
    public void testConnection_2users() throws Exception {
        SevenWondersSession session1 = client.connect(serverUrl);
        SevenWondersSession session2 = client.connect(serverUrl);
        session1.chooseName("Player1");
        session2.chooseName( "Player2");

        ApiLobby lobby = session1.createGame("Test Game");
        session2.joinGame(lobby.getId());

        SevenWondersSession session3 = client.connect(serverUrl);
        session3.chooseName("Player3");
        session3.joinGame(lobby.getId());

        session1.startGame(lobby.getId());

        disconnect(session1, session2, session3);
    }

    @After
    public void tearDown() {
        client.stop();
    }
}

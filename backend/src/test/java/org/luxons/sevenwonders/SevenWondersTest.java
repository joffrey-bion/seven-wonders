package org.luxons.sevenwonders;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.hildan.jackstomp.Channel;
import org.hildan.jackstomp.JackstompSession;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.test.api.ApiLobby;
import org.luxons.sevenwonders.test.api.ApiPlayer;
import org.luxons.sevenwonders.test.api.ApiPlayerTurnInfo;
import org.luxons.sevenwonders.test.api.SevenWondersClient;
import org.luxons.sevenwonders.test.api.SevenWondersSession;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
    public void chooseName() throws InterruptedException, ExecutionException, TimeoutException {
        SevenWondersSession session = client.connect(serverUrl);
        String playerName = "Test User";
        ApiPlayer player = session.chooseName(playerName);
        assertNotNull(player);
        assertEquals(playerName, player.getDisplayName());
        session.disconnect();
    }

    private SevenWondersSession newPlayer(String name) throws InterruptedException, TimeoutException,
            ExecutionException {
        SevenWondersSession otherSession = client.connect(serverUrl);
        otherSession.chooseName(name);
        return otherSession;
    }

    @Test
    public void lobbySubscription_ignoredForOutsiders() throws InterruptedException, ExecutionException,
            TimeoutException {
        SevenWondersSession ownerSession = newPlayer("GameOwner");
        SevenWondersSession session1 = newPlayer("Player1");
        SevenWondersSession session2 = newPlayer("Player2");
        String gameName = "Test Game";
        ApiLobby lobby = ownerSession.createGame(gameName);
        session1.joinGame(lobby.getId());
        session2.joinGame(lobby.getId());

        SevenWondersSession outsiderSession = newPlayer("Outsider");
        JackstompSession session = outsiderSession.getJackstompSession();
        Channel<Object> started = session.subscribeEmptyMsgs("/topic/lobby/" + lobby.getId() + "/started");

        ownerSession.startGame(lobby.getId());
        Object nothing = started.next(1, TimeUnit.SECONDS);
        assertNull(nothing);
        disconnect(ownerSession, session1, session2, outsiderSession);
    }

    @Test
    public void createGame_success() throws InterruptedException, ExecutionException, TimeoutException {
        SevenWondersSession ownerSession = newPlayer("GameOwner");

        String gameName = "Test Game";
        ApiLobby lobby = ownerSession.createGame(gameName);
        assertNotNull(lobby);
        assertEquals(gameName, lobby.getName());

        disconnect(ownerSession);
    }

    @Test
    public void createGame_seenByConnectedPlayers() throws InterruptedException, ExecutionException, TimeoutException {
        SevenWondersSession otherSession = newPlayer("OtherPlayer");
        Channel<ApiLobby[]> games = otherSession.watchGames();

        ApiLobby[] receivedLobbies = games.next();
        assertNotNull(receivedLobbies);
        assertEquals(0, receivedLobbies.length);

        SevenWondersSession ownerSession = newPlayer("GameOwner");
        String gameName = "Test Game";
        ApiLobby createdLobby = ownerSession.createGame(gameName);

        receivedLobbies = games.next();
        assertNotNull(receivedLobbies);
        assertEquals(1, receivedLobbies.length);
        ApiLobby receivedLobby = receivedLobbies[0];
        assertEquals(createdLobby.getId(), receivedLobby.getId());
        assertEquals(createdLobby.getName(), receivedLobby.getName());

        disconnect(ownerSession, otherSession);
    }

    @Test
    public void startGame_3players() throws Exception {
        SevenWondersSession session1 = newPlayer("Player1");
        SevenWondersSession session2 = newPlayer("Player2");

        ApiLobby lobby = session1.createGame("Test Game");
        session2.joinGame(lobby.getId());

        SevenWondersSession session3 = newPlayer("Player3");
        session3.joinGame(lobby.getId());

        session1.startGame(lobby.getId());

        Channel<ApiPlayerTurnInfo> turns1 = session1.watchTurns();
        Channel<ApiPlayerTurnInfo> turns2 = session2.watchTurns();
        Channel<ApiPlayerTurnInfo> turns3 = session3.watchTurns();
        session1.sayReady();
        session2.sayReady();
        session3.sayReady();
        ApiPlayerTurnInfo turn1 = turns1.next();
        ApiPlayerTurnInfo turn2 = turns2.next();
        ApiPlayerTurnInfo turn3 = turns3.next();
        assertNotNull(turn1);
        assertNotNull(turn2);
        assertNotNull(turn3);

        disconnect(session1, session2, session3);
    }

    @After
    public void tearDown() {
        client.stop();
    }
}

package org.luxons.sevenwonders.test.api;

import org.luxons.sevenwonders.actions.ChooseNameAction;
import org.luxons.sevenwonders.actions.CreateGameAction;
import org.luxons.sevenwonders.actions.JoinGameAction;
import org.luxons.sevenwonders.test.client.JackstompSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SevenWondersSession {

    private final JackstompSession session;

    public SevenWondersSession(JackstompSession session) {
        this.session = session;
    }

    public void disconnect() {
        session.disconnect();
    }

    public ApiPlayer chooseName(String displayName) throws Exception {
        ChooseNameAction action = new ChooseNameAction();
        action.setPlayerName(displayName);

        ApiPlayer player = session.request(action, ApiPlayer.class, "/app/chooseName", "/user/queue/nameChoice");
        assertNotNull(player);
        assertEquals(displayName, player.getDisplayName());

        return player;
    }

    public ApiLobby createGame(String gameName) throws InterruptedException {
        CreateGameAction action = new CreateGameAction();
        action.setGameName(gameName);

        ApiLobby lobby = session.request(action, ApiLobby.class, "/app/lobby/create", "/user/queue/lobby/joined");
        assertNotNull(lobby);
        assertEquals(gameName, lobby.getName());
        return lobby;
    }

    public ApiLobby joinGame(long gameId) throws InterruptedException {
        JoinGameAction action = new JoinGameAction();
        action.setGameId(gameId);

        ApiLobby lobby = session.request(action, ApiLobby.class, "/app/lobby/join", "/user/queue/lobby/joined");
        assertNotNull(lobby);
        assertEquals(gameId, lobby.getId());
        return lobby;
    }

    public void startGame(long gameId) throws InterruptedException {
        String sendDestination = "/app/lobby/startGame";
        String receiveDestination = "/topic/lobby/" + gameId + "/started";
        boolean received = session.request(sendDestination, null, receiveDestination);
        assertTrue(received);
    }
}

package org.luxons.sevenwonders.test.api;

import org.hildan.jackstomp.Channel;
import org.hildan.jackstomp.JackstompSession;
import org.luxons.sevenwonders.actions.ChooseNameAction;
import org.luxons.sevenwonders.actions.CreateGameAction;
import org.luxons.sevenwonders.actions.JoinGameAction;
import org.luxons.sevenwonders.errors.UIError;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SevenWondersSession {

    private final JackstompSession session;

    public SevenWondersSession(JackstompSession session) {
        this.session = session;
    }

    public JackstompSession getJackstompSession() {
        return session;
    }

    public void disconnect() {
        session.disconnect();
    }

    public Channel<UIError> watchErrors() {
        return session.subscribe("/user/queue/errors", UIError.class);
    }

    public ApiPlayer chooseName(String displayName) throws InterruptedException {
        ChooseNameAction action = new ChooseNameAction();
        action.setPlayerName(displayName);
        return session.request(action, ApiPlayer.class, "/app/chooseName", "/user/queue/nameChoice");
    }

    public Channel<ApiLobby[]> watchGames() {
        return session.subscribe("/topic/games", ApiLobby[].class);
    }

    public ApiLobby createGame(String gameName) throws InterruptedException {
        CreateGameAction action = new CreateGameAction();
        action.setGameName(gameName);

        return session.request(action, ApiLobby.class, "/app/lobby/create", "/user/queue/lobby/joined");
    }

    public ApiLobby joinGame(long gameId) throws InterruptedException {
        JoinGameAction action = new JoinGameAction();
        action.setGameId(gameId);

        ApiLobby lobby = session.request(action, ApiLobby.class, "/app/lobby/join", "/user/queue/lobby/joined");
        assertNotNull(lobby);
        assertEquals(gameId, lobby.getId());
        return lobby;
    }

    public Channel<ApiLobby> watchLobbyUpdates(long gameId) {
        return session.subscribe("/topic/lobby/" + gameId + "/updated", ApiLobby.class);
    }

    public Channel<ApiLobby> watchLobbyStart(long gameId) {
        return session.subscribe("/topic/lobby/" + gameId + "/started", ApiLobby.class);
    }

    public void startGame(long gameId) throws InterruptedException {
        String sendDestination = "/app/lobby/startGame";
        String receiveDestination = "/topic/lobby/" + gameId + "/started";
        boolean received = session.request(null, sendDestination, receiveDestination);
        assertTrue(received);
    }

    public void sayReady() {
        session.send("/app/game/sayReady", null);
    }

    public Channel<ApiPlayerTurnInfo> watchTurns() {
        return session.subscribe("/user/queue/game/turn", ApiPlayerTurnInfo.class);
    }
}

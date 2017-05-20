package org.luxons.sevenwonders.lobby;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.luxons.sevenwonders.game.Game;

public class Player {

    private final String username;

    private String displayName;

    private int index;

    private boolean ready;

    private transient Lobby lobby;

    private transient Game game;

    public Player(String username, String displayName) {
        this.username = username;
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @JsonIgnore
    public Lobby getLobby() {
        return lobby;
    }

    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    @JsonIgnore
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public String toString() {
        return "'" + displayName + "' (" + username + ")";
    }
}

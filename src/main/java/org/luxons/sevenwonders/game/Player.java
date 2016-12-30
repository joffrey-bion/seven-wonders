package org.luxons.sevenwonders.game;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Player {

    private final String userName;

    private String displayName;

    private int index;

    private transient Lobby lobby;

    private transient Game game;

    public Player(String userName) {
        this.userName = userName;
    }

    public Player(String displayName, String userName) {
        this(userName);
        setDisplayName(displayName);
    }

    public String getUserName() {
        return userName;
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
}

package org.luxons.sevenwonders.game;

public class Player {

    private final String displayName;

    private final String userName;

    private int index;

    public Player(String displayName, String userName) {
        this.displayName = displayName;
        this.userName = userName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUserName() {
        return userName;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}

package org.luxons.sevenwonders.game;

public class Player {

    private final String displayName;

    private final String userName;

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

    @Override
    public String toString() {
        return "Player{" + "name='" + displayName + '\'' + ", userName='" + userName + '\'' + '}';
    }
}

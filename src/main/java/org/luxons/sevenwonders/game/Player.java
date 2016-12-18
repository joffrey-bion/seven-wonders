package org.luxons.sevenwonders.game;

public class Player {

    private String displayName;

    private String userName;

    public Player(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Player{" + "name='" + displayName + '\'' + ", userName='" + userName + '\'' + '}';
    }
}

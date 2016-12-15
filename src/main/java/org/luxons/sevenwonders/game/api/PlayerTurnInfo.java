package org.luxons.sevenwonders.game.api;

import java.util.List;

public class PlayerTurnInfo {

    private final int playerIndex;

    private final Table table;

    private List<HandCard> hand;

    private String message;

    public PlayerTurnInfo(int playerIndex, Table table) {
        this.playerIndex = playerIndex;
        this.table = table;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public Table getTable() {
        return table;
    }

    public List<HandCard> getHand() {
        return hand;
    }

    public void setHand(List<HandCard> hand) {
        this.hand = hand;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

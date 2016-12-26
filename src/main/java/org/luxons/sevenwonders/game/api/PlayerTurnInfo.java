package org.luxons.sevenwonders.game.api;

import java.util.List;

import org.luxons.sevenwonders.game.Player;

public class PlayerTurnInfo {

    private final Player player;

    private final Table table;

    private int currentAge;

    private List<HandCard> hand;

    private String message;

    public PlayerTurnInfo(Player player, Table table) {
        this.player = player;
        this.table = table;
    }

    public Player getPlayer() {
        return player;
    }

    public Table getTable() {
        return table;
    }

    public int getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
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

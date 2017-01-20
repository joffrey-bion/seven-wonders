package org.luxons.sevenwonders.game.api;

import java.util.List;

import org.luxons.sevenwonders.game.Player;
import org.luxons.sevenwonders.game.cards.HandRotationDirection;

public class PlayerTurnInfo {

    private final Player player;

    private final Table table;

    private int currentAge;

    private HandRotationDirection handRotationDirection;

    private Action action;

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

    public HandRotationDirection getHandRotationDirection() {
        return handRotationDirection;
    }

    public void setHandRotationDirection(HandRotationDirection handRotationDirection) {
        this.handRotationDirection = handRotationDirection;
    }

    public List<HandCard> getHand() {
        return hand;
    }

    public void setHand(List<HandCard> hand) {
        this.hand = hand;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

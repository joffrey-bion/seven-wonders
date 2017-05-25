package org.luxons.sevenwonders.test.api;

import java.util.List;
import java.util.Objects;

import org.luxons.sevenwonders.game.api.Action;

public class ApiPlayerTurnInfo {

    private int playerIndex;

    private ApiTable table;

    private int currentAge;

    private Action action;

    private List<ApiHandCard> hand;

    private List<ApiCard> neighbourGuildCards;

    private String message;

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public ApiTable getTable() {
        return table;
    }

    public void setTable(ApiTable table) {
        this.table = table;
    }

    public int getCurrentAge() {
        return currentAge;
    }

    public void setCurrentAge(int currentAge) {
        this.currentAge = currentAge;
    }

    public List<ApiHandCard> getHand() {
        return hand;
    }

    public void setHand(List<ApiHandCard> hand) {
        this.hand = hand;
    }

    public List<ApiCard> getNeighbourGuildCards() {
        return neighbourGuildCards;
    }

    public void setNeighbourGuildCards(List<ApiCard> neighbourGuildCards) {
        this.neighbourGuildCards = neighbourGuildCards;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApiPlayerTurnInfo that = (ApiPlayerTurnInfo) o;
        return playerIndex == that.playerIndex && currentAge == that.currentAge && Objects.equals(table, that.table)
                && action == that.action && Objects.equals(hand, that.hand) && Objects.equals(neighbourGuildCards,
                that.neighbourGuildCards) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerIndex, table, currentAge, action, hand, neighbourGuildCards, message);
    }
}

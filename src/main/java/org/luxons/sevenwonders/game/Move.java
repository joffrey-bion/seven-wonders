package org.luxons.sevenwonders.game;

import java.util.HashMap;
import java.util.Map;

import org.luxons.sevenwonders.game.resources.Resources;

public class Move {

    private String cardName;

    private MoveType moveType;

    private Map<Integer, Resources> boughtResources = new HashMap<>();

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public MoveType getType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    public Map<Integer, Resources> getBoughtResources() {
        return boughtResources;
    }

    public void setBoughtResources(Map<Integer, Resources> boughtResources) {
        this.boughtResources = boughtResources;
    }
}

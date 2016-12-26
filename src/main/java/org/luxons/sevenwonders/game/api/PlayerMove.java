package org.luxons.sevenwonders.game.api;

import java.util.ArrayList;
import java.util.List;

import org.luxons.sevenwonders.game.moves.MoveType;
import org.luxons.sevenwonders.game.resources.BoughtResources;

public class PlayerMove {

    private String cardName;

    private MoveType type;

    private List<BoughtResources> boughtResources = new ArrayList<>();

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public MoveType getType() {
        return type;
    }

    public void setType(MoveType type) {
        this.type = type;
    }

    public List<BoughtResources> getBoughtResources() {
        return boughtResources;
    }

    public void setBoughtResources(List<BoughtResources> boughtResources) {
        this.boughtResources = boughtResources;
    }
}

package org.luxons.sevenwonders.test.api;

import java.util.Map;

import org.luxons.sevenwonders.game.boards.ScienceType;

public class ApiScience {

    private Map<ScienceType, Integer> quantities;

    private int jokers;

    public Map<ScienceType, Integer> getQuantities() {
        return quantities;
    }

    public void setQuantities(Map<ScienceType, Integer> quantities) {
        this.quantities = quantities;
    }

    public int getJokers() {
        return jokers;
    }

    public void setJokers(int jokers) {
        this.jokers = jokers;
    }
}

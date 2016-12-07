package org.luxons.sevenwonders.game;

public class Settings {

    private int initialGold = 3;

    private int defaultTradingCost = 2;

    public int getInitialGold() {
        return initialGold;
    }

    public void setInitialGold(int initialGold) {
        this.initialGold = initialGold;
    }

    public int getDefaultTradingCost() {
        return defaultTradingCost;
    }

    public void setDefaultTradingCost(int defaultTradingCost) {
        this.defaultTradingCost = defaultTradingCost;
    }
}

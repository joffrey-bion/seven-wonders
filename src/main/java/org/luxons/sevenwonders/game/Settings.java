package org.luxons.sevenwonders.game;

import java.util.Random;

import org.luxons.sevenwonders.game.wonders.WonderSide;

public class Settings {

    private int nbPlayers = -1;

    private int initialGold = 3;

    private int discardedCardGold = 3;

    private int defaultTradingCost = 2;

    private WonderSide wonderSide = WonderSide.A;

    private long randomSeedForTests = -1;

    public int getNbPlayers() {
        if (nbPlayers < 0) {
            throw new IllegalStateException("The number of players has not been initialized");
        }
        return nbPlayers;
    }

    public void setNbPlayers(int nbPlayers) {
        this.nbPlayers = nbPlayers;
    }

    public int getInitialGold() {
        return initialGold;
    }

    public void setInitialGold(int initialGold) {
        this.initialGold = initialGold;
    }

    public int getDiscardedCardGold() {
        return discardedCardGold;
    }

    public void setDiscardedCardGold(int discardedCardGold) {
        this.discardedCardGold = discardedCardGold;
    }

    public int getDefaultTradingCost() {
        return defaultTradingCost;
    }

    public void setDefaultTradingCost(int defaultTradingCost) {
        this.defaultTradingCost = defaultTradingCost;
    }

    public WonderSide getWonderSide() {
        return wonderSide;
    }

    public void setWonderSide(WonderSide wonderSide) {
        this.wonderSide = wonderSide;
    }

    public long getRandomSeedForTests() {
        return randomSeedForTests;
    }

    public void setRandomSeedForTests(long randomSeedForTests) {
        this.randomSeedForTests = randomSeedForTests;
    }

    public Random getRandom() {
        return randomSeedForTests > 0 ? new Random(randomSeedForTests) : new Random();
    }
}

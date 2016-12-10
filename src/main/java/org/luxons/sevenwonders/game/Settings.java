package org.luxons.sevenwonders.game;

import java.util.Random;

import org.luxons.sevenwonders.game.wonders.WonderSide;

public class Settings {

    private int nbPlayers = 4;

    private int initialGold = 3;

    private int defaultTradingCost = 2;

    private WonderSide wonderSide = WonderSide.A;

    private Integer nbGuildCards = null;

    private long randomSeedForTests = 0;

    public int getNbPlayers() {
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

    public int getNbGuildCards() {
        return nbGuildCards == null ? nbPlayers + 2 : nbGuildCards;
    }

    public void setNbGuildCards(int nbGuildCards) {
        this.nbGuildCards = nbGuildCards;
    }

    public long getRandomSeedForTests() {
        return randomSeedForTests;
    }

    public void setRandomSeedForTests(long randomSeedForTests) {
        this.randomSeedForTests = randomSeedForTests;
    }

    public Random getRandom() {
        return new Random(randomSeedForTests);
    }
}

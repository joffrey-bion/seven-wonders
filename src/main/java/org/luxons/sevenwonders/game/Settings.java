package org.luxons.sevenwonders.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.luxons.sevenwonders.game.data.definitions.WonderSide;
import org.luxons.sevenwonders.game.data.definitions.WonderSidePickMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Settings {

    private int nbPlayers = -1;

    private int initialGold = 3;

    private int discardedCardGold = 3;

    private int defaultTradingCost = 2;

    private WonderSidePickMethod wonderSidePickMethod = WonderSidePickMethod.EACH_RANDOM;

    private transient WonderSide lastPickedSide = null;

    private long randomSeedForTests = -1;

    private transient Random random;

    private int lostPointsPerDefeat = 1;

    private Map<Integer, Integer> wonPointsPerVictoryPerAge = new HashMap<>();

    public Settings() {
        wonPointsPerVictoryPerAge.put(1, 1);
        wonPointsPerVictoryPerAge.put(2, 3);
        wonPointsPerVictoryPerAge.put(3, 5);
    }

    @JsonIgnore
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

    public WonderSidePickMethod getWonderSidePickMethod() {
        return wonderSidePickMethod;
    }

    public void setWonderSidePickMethod(WonderSidePickMethod wonderSidePickMethod) {
        this.wonderSidePickMethod = wonderSidePickMethod;
    }

    public WonderSide pickWonderSide() {
        return lastPickedSide = wonderSidePickMethod.pickSide(getRandom(), lastPickedSide);
    }

    public int getLostPointsPerDefeat() {
        return lostPointsPerDefeat;
    }

    public void setLostPointsPerDefeat(int lostPointsPerDefeat) {
        this.lostPointsPerDefeat = lostPointsPerDefeat;
    }

    public Map<Integer, Integer> getWonPointsPerVictoryPerAge() {
        return wonPointsPerVictoryPerAge;
    }

    public void setWonPointsPerVictoryPerAge(Map<Integer, Integer> wonPointsPerVictoryPerAge) {
        this.wonPointsPerVictoryPerAge = wonPointsPerVictoryPerAge;
    }

    public long getRandomSeedForTests() {
        return randomSeedForTests;
    }

    public void setRandomSeedForTests(long randomSeedForTests) {
        this.randomSeedForTests = randomSeedForTests;
    }

    @JsonIgnore
    public Random getRandom() {
        if (random == null) {
            random = randomSeedForTests > 0 ? new Random(randomSeedForTests) : new Random();
        }
        return random;
    }
}

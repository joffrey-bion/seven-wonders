package org.luxons.sevenwonders.game.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.luxons.sevenwonders.game.data.definitions.WonderSidePickMethod;

public class CustomizableSettings {

    private long randomSeedForTests = -1;

    private int timeLimitInSeconds = 45;

    private WonderSidePickMethod wonderSidePickMethod = WonderSidePickMethod.EACH_RANDOM;

    private int initialGold = 3;

    private int discardedCardGold = 3;

    private int defaultTradingCost = 2;

    private int pointsPer3Gold = 1;

    private int lostPointsPerDefeat = 1;

    private Map<Integer, Integer> wonPointsPerVictoryPerAge = new HashMap<>();

    public CustomizableSettings() {
        wonPointsPerVictoryPerAge.put(1, 1);
        wonPointsPerVictoryPerAge.put(2, 3);
        wonPointsPerVictoryPerAge.put(3, 5);
    }

    public long getRandomSeedForTests() {
        return randomSeedForTests;
    }

    public void setRandomSeedForTests(long randomSeedForTests) {
        this.randomSeedForTests = randomSeedForTests;
    }

    public int getTimeLimitInSeconds() {
        return timeLimitInSeconds;
    }

    public void setTimeLimitInSeconds(int timeLimitInSeconds) {
        this.timeLimitInSeconds = timeLimitInSeconds;
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

    public int getPointsPer3Gold() {
        return pointsPer3Gold;
    }

    public void setPointsPer3Gold(int pointsPer3Gold) {
        this.pointsPer3Gold = pointsPer3Gold;
    }

    public WonderSidePickMethod getWonderSidePickMethod() {
        return wonderSidePickMethod;
    }

    public void setWonderSidePickMethod(WonderSidePickMethod wonderSidePickMethod) {
        this.wonderSidePickMethod = wonderSidePickMethod;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CustomizableSettings that = (CustomizableSettings) o;
        return randomSeedForTests == that.randomSeedForTests && timeLimitInSeconds == that.timeLimitInSeconds
                && initialGold == that.initialGold && discardedCardGold == that.discardedCardGold
                && defaultTradingCost == that.defaultTradingCost && pointsPer3Gold == that.pointsPer3Gold
                && lostPointsPerDefeat == that.lostPointsPerDefeat && wonderSidePickMethod == that.wonderSidePickMethod
                && Objects.equals(wonPointsPerVictoryPerAge, that.wonPointsPerVictoryPerAge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(randomSeedForTests, timeLimitInSeconds, wonderSidePickMethod, initialGold,
                discardedCardGold, defaultTradingCost, pointsPer3Gold, lostPointsPerDefeat, wonPointsPerVictoryPerAge);
    }
}

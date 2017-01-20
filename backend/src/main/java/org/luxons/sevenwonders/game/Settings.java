package org.luxons.sevenwonders.game;

import java.util.Map;
import java.util.Random;

import org.luxons.sevenwonders.game.api.CustomizableSettings;
import org.luxons.sevenwonders.game.data.definitions.WonderSide;
import org.luxons.sevenwonders.game.data.definitions.WonderSidePickMethod;

public class Settings {

    private final Random random;

    private final int nbPlayers;

    private final int initialGold;

    private final int discardedCardGold;

    private final int defaultTradingCost;

    private final int pointsPer3Gold;

    private final WonderSidePickMethod wonderSidePickMethod;

    private WonderSide lastPickedSide = null;

    private final int lostPointsPerDefeat;

    private final Map<Integer, Integer> wonPointsPerVictoryPerAge;

    public Settings(int nbPlayers) {
        this(nbPlayers, new CustomizableSettings());
    }

    public Settings(int nbPlayers, CustomizableSettings customSettings) {
        long seed = customSettings.getRandomSeedForTests();
        this.random = seed > 0 ? new Random(seed) : new Random();
        this.nbPlayers = nbPlayers;
        this.initialGold = customSettings.getInitialGold();
        this.discardedCardGold = customSettings.getDiscardedCardGold();
        this.defaultTradingCost = customSettings.getDefaultTradingCost();
        this.pointsPer3Gold = customSettings.getPointsPer3Gold();
        this.wonderSidePickMethod = customSettings.getWonderSidePickMethod();
        this.lostPointsPerDefeat = customSettings.getLostPointsPerDefeat();
        this.wonPointsPerVictoryPerAge = customSettings.getWonPointsPerVictoryPerAge();
    }

    public Random getRandom() {
        return random;
    }

    public int getNbPlayers() {
        return nbPlayers;
    }

    public int getInitialGold() {
        return initialGold;
    }

    public int getDiscardedCardGold() {
        return discardedCardGold;
    }

    public int getDefaultTradingCost() {
        return defaultTradingCost;
    }

    public int getPointsPer3Gold() {
        return pointsPer3Gold;
    }

    public WonderSide pickWonderSide() {
        return lastPickedSide = wonderSidePickMethod.pickSide(getRandom(), lastPickedSide);
    }

    public int getLostPointsPerDefeat() {
        return lostPointsPerDefeat;
    }

    public Map<Integer, Integer> getWonPointsPerVictoryPerAge() {
        return wonPointsPerVictoryPerAge;
    }
}

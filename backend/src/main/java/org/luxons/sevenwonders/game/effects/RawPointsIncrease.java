package org.luxons.sevenwonders.game.effects;

import java.util.Objects;

import org.luxons.sevenwonders.game.api.Table;

public class RawPointsIncrease extends EndGameEffect {

    private final int points;

    public int getPoints() {
        return points;
    }

    public RawPointsIncrease(int points) {
        this.points = points;
    }

    @Override
    public int computePoints(Table table, int playerIndex) {
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RawPointsIncrease that = (RawPointsIncrease) o;
        return points == that.points;
    }

    @Override
    public int hashCode() {
        return Objects.hash(points);
    }
}

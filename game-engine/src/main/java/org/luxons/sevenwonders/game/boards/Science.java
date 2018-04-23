package org.luxons.sevenwonders.game.boards;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

public class Science {

    private Map<ScienceType, Integer> quantities = new EnumMap<>(ScienceType.class);

    private int jokers;

    public void add(ScienceType type, int quantity) {
        quantities.merge(type, quantity, (x, y) -> x + y);
    }

    public void addJoker(int quantity) {
        jokers += quantity;
    }

    public int getJokers() {
        return jokers;
    }

    public void addAll(Science science) {
        science.quantities.forEach(this::add);
        jokers += science.jokers;
    }

    public int getQuantity(ScienceType type) {
        return quantities.getOrDefault(type, 0);
    }

    public int size() {
        return quantities.values().stream().mapToInt(q -> q).sum() + jokers;
    }

    public int computePoints() {
        ScienceType[] types = ScienceType.values();
        Integer[] values = new Integer[types.length];
        for (int i = 0; i < types.length; i++) {
            values[i] = quantities.getOrDefault(types[i], 0);
        }
        return computePoints(values, jokers);
    }

    private static int computePoints(Integer[] values, int jokers) {
        if (jokers == 0) {
            return computePointsNoJoker(values);
        }
        int maxPoints = 0;
        for (int i = 0; i < values.length; i++) {
            values[i]++;
            maxPoints = Math.max(maxPoints, computePoints(values, jokers - 1));
            values[i]--;
        }
        return maxPoints;
    }

    private static int computePointsNoJoker(Integer[] values) {
        int independentSquaresSum = Arrays.stream(values).mapToInt(i -> i * i).sum();
        int nbGroupsOfAll = Arrays.stream(values).mapToInt(i -> i).min().orElse(0);
        return independentSquaresSum + nbGroupsOfAll * 7;
    }
}

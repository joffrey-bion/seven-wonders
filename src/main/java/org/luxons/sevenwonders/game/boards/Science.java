package org.luxons.sevenwonders.game.boards;

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

    public void addAll(Science science) {
        science.quantities.forEach(this::add);
        jokers += science.jokers;
    }
}

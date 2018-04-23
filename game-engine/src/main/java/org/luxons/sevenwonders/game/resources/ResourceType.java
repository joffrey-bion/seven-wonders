package org.luxons.sevenwonders.game.resources;

import java.util.HashMap;
import java.util.Map;

public enum ResourceType {
    WOOD('W'),
    STONE('S'),
    ORE('O'),
    CLAY('C'),
    GLASS('G'),
    PAPYRUS('P'),
    LOOM('L');

    private static final Map<Character, ResourceType> typesPerSymbol = new HashMap<>(7);

    static {
        for (ResourceType type : values()) {
            typesPerSymbol.put(type.symbol, type);
        }
    }

    private final Character symbol;

    ResourceType(Character symbol) {
        this.symbol = symbol;
    }

    public static ResourceType fromSymbol(Character symbol) {
        ResourceType type = typesPerSymbol.get(symbol);
        if (type == null) {
            throw new IllegalArgumentException(String.format("Unknown resource type symbol '%s'", symbol));
        }
        return type;
    }

    public Character getSymbol() {
        return symbol;
    }
}

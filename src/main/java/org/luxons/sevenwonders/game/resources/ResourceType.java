package org.luxons.sevenwonders.game.resources;

public enum ResourceType {
    WOOD("W"),
    STONE("S"),
    ORE("O"),
    CLAY("C"),
    GLASS("G"),
    PAPYRUS("P"),
    LOOM("L");

    private final String symbol;

    ResourceType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}

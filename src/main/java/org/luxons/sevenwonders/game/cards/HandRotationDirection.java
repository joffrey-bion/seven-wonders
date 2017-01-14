package org.luxons.sevenwonders.game.cards;

public enum HandRotationDirection {
    LEFT(-1), RIGHT(1);

    private final int indexOffset;

    HandRotationDirection(int i) {
        this.indexOffset = i;
    }

    public int getIndexOffset() {
        return indexOffset;
    }
}

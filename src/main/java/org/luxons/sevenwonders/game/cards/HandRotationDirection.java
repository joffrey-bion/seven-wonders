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

    public static HandRotationDirection forAge(int age) {
        // clockwise at age 1, and alternating
        return age % 2 == 0 ? HandRotationDirection.LEFT : HandRotationDirection.RIGHT;
    }
}

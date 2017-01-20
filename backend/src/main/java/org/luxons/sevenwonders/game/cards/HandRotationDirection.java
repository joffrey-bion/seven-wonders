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
        // clockwise (pass to the left) at age 1, and alternating
        return age % 2 == 0 ? HandRotationDirection.RIGHT : HandRotationDirection.LEFT;
    }
}

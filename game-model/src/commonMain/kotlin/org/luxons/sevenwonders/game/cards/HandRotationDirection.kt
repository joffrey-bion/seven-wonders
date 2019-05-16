package org.luxons.sevenwonders.game.cards

enum class HandRotationDirection {
    LEFT,
    RIGHT;

    companion object {
        // clockwise (pass to the left) at age 1, and alternating
        fun forAge(age: Int): HandRotationDirection = if (age % 2 == 0) RIGHT else LEFT
    }
}

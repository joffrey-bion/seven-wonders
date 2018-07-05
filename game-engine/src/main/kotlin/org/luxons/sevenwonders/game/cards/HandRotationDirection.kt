package org.luxons.sevenwonders.game.cards

enum class HandRotationDirection {
    LEFT,
    RIGHT;

    companion object {

        fun forAge(age: Int): HandRotationDirection {
            // clockwise (pass to the left) at age 1, and alternating
            return if (age % 2 == 0) HandRotationDirection.RIGHT else HandRotationDirection.LEFT
        }
    }
}


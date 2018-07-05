package org.luxons.sevenwonders.game.data

import java.util.Random

enum class WonderSidePickMethod {
    ALL_A {
        override fun pickSide(random: Random, lastPickedSide: WonderSide?): WonderSide {
            return WonderSide.A
        }
    },
    ALL_B {
        override fun pickSide(random: Random, lastPickedSide: WonderSide?): WonderSide {
            return WonderSide.B
        }
    },
    EACH_RANDOM {
        override fun pickSide(random: Random, lastPickedSide: WonderSide?): WonderSide {
            return if (random.nextBoolean()) WonderSide.A else WonderSide.B
        }
    },
    SAME_RANDOM_FOR_ALL {
        override fun pickSide(random: Random, lastPickedSide: WonderSide?): WonderSide {
            return lastPickedSide ?: if (random.nextBoolean()) WonderSide.A else WonderSide.B
        }
    };

    abstract fun pickSide(random: Random, lastPickedSide: WonderSide?): WonderSide
}

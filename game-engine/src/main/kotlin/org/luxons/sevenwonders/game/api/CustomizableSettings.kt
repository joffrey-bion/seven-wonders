package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.data.definitions.WonderSide
import java.util.Random

data class CustomizableSettings(
    val randomSeedForTests: Long? = null,
    val timeLimitInSeconds: Int = 45,
    val wonderSidePickMethod: WonderSidePickMethod = WonderSidePickMethod.EACH_RANDOM,
    val initialGold: Int = 3,
    val discardedCardGold: Int = 3,
    val defaultTradingCost: Int = 2,
    val pointsPer3Gold: Int = 1,
    val lostPointsPerDefeat: Int = 1,
    val wonPointsPerVictoryPerAge: Map<Int, Int> = mapOf(1 to 1, 2 to 3, 3 to 5)
)

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

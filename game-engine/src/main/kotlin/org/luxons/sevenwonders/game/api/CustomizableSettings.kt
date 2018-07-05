package org.luxons.sevenwonders.game.api

import org.luxons.sevenwonders.game.data.definitions.WonderSidePickMethod

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

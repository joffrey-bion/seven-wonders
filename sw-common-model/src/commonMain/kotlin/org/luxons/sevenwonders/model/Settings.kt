package org.luxons.sevenwonders.model

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class Settings(
    val randomSeedForTests: Long? = null,
    val timeLimitInSeconds: Int = 45,
    val initialGold: Int = 3,
    val discardedCardGold: Int = 3,
    val defaultTradingCost: Int = 2,
    val pointsPer3Gold: Int = 1,
    val lostPointsPerDefeat: Int = 1,
    val wonPointsPerVictoryPerAge: Map<Int, Int> = mapOf(1 to 1, 2 to 3, 3 to 5),
) {
    val random: Random by lazy { randomSeedForTests?.let { Random(it) } ?: Random }
}

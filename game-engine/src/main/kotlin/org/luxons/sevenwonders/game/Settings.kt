package org.luxons.sevenwonders.game

import org.luxons.sevenwonders.game.api.CustomizableSettings
import org.luxons.sevenwonders.game.data.WonderSide
import org.luxons.sevenwonders.game.data.WonderSidePickMethod
import java.util.Random

class Settings @JvmOverloads constructor(
    val nbPlayers: Int,
    customSettings: CustomizableSettings = CustomizableSettings()
) {
    val random: Random
    val timeLimitInSeconds: Int = customSettings.timeLimitInSeconds
    val initialGold: Int = customSettings.initialGold
    val discardedCardGold: Int = customSettings.discardedCardGold
    val defaultTradingCost: Int = customSettings.defaultTradingCost
    val pointsPer3Gold: Int = customSettings.pointsPer3Gold
    val lostPointsPerDefeat: Int = customSettings.lostPointsPerDefeat
    val wonPointsPerVictoryPerAge: Map<Int, Int> = customSettings.wonPointsPerVictoryPerAge

    private val wonderSidePickMethod: WonderSidePickMethod = customSettings.wonderSidePickMethod
    private var lastPickedSide: WonderSide? = null

    init {
        val seed = customSettings.randomSeedForTests
        this.random = if (seed != null) Random(seed) else Random()
    }

    fun pickWonderSide(): WonderSide {
        val newSide = wonderSidePickMethod.pickSide(random, lastPickedSide)
        lastPickedSide = newSide
        return newSide
    }
}

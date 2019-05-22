package org.luxons.sevenwonders.engine

import org.luxons.sevenwonders.model.CustomizableSettings
import org.luxons.sevenwonders.model.WonderSide
import org.luxons.sevenwonders.model.WonderSidePickMethod
import kotlin.random.Random

internal class Settings(
    val nbPlayers: Int,
    customSettings: CustomizableSettings = CustomizableSettings()
) {
    val random: Random = customSettings.randomSeedForTests?.let { Random(it) } ?: Random
    val timeLimitInSeconds: Int = customSettings.timeLimitInSeconds
    val initialGold: Int = customSettings.initialGold
    val discardedCardGold: Int = customSettings.discardedCardGold
    val defaultTradingCost: Int = customSettings.defaultTradingCost
    val pointsPer3Gold: Int = customSettings.pointsPer3Gold
    val lostPointsPerDefeat: Int = customSettings.lostPointsPerDefeat
    val wonPointsPerVictoryPerAge: Map<Int, Int> = customSettings.wonPointsPerVictoryPerAge

    private val wonderSidePickMethod: WonderSidePickMethod = customSettings.wonderSidePickMethod
    private var lastPickedSide: WonderSide? = null

    fun pickWonderSide(): WonderSide {
        val newSide = wonderSidePickMethod.pickSide(random, lastPickedSide)
        lastPickedSide = newSide
        return newSide
    }
}

package org.luxons.sevenwonders.game.boards

import org.luxons.sevenwonders.game.data.Age

class Military internal constructor(
    private val lostPointsPerDefeat: Int,
    private val wonPointsPerVictoryPerAge: Map<Age, Int>
) {
    var nbShields = 0
        private set

    var totalPoints = 0
        private set

    var nbDefeatTokens = 0
        private set

    fun addShields(nbShields: Int) {
        this.nbShields += nbShields
    }

    fun victory(age: Age) {
        val wonPoints = wonPointsPerVictoryPerAge[age] ?: throw UnknownAgeException(age)
        totalPoints += wonPoints
    }

    fun defeat() {
        totalPoints -= lostPointsPerDefeat
        nbDefeatTokens++
    }

    internal class UnknownAgeException(unknownAge: Age) : IllegalArgumentException(unknownAge.toString())
}

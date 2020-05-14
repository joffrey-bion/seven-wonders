package org.luxons.sevenwonders.model.score

import kotlinx.serialization.Serializable

@Serializable
class ScoreBoard(val scores: Collection<PlayerScore>)

@Serializable
data class PlayerScore(
    val boardGold: Int,
    val pointsByCategory: Map<ScoreCategory, Int>
) : Comparable<PlayerScore> {

    val totalPoints = pointsByCategory.values.sum()

    override fun compareTo(other: PlayerScore) = compareValuesBy(this, other, { it.totalPoints }, { it.boardGold })
}

enum class ScoreCategory {
    CIVIL,
    SCIENCE,
    MILITARY,
    TRADE,
    GUILD,
    WONDER,
    GOLD
}

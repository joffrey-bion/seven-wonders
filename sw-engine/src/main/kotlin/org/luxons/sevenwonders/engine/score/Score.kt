package org.luxons.sevenwonders.engine.score

class ScoreBoard(scores: Collection<PlayerScore>) {

    val scores: Collection<PlayerScore> = scores.sortedDescending()
}

data class PlayerScore(val boardGold: Int, val pointsByCategory: Map<ScoreCategory, Int>) : Comparable<PlayerScore> {

    val totalPoints = pointsByCategory.map { it.value }.sum()

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

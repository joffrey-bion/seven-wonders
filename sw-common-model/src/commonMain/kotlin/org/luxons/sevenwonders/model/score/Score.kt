package org.luxons.sevenwonders.model.score

import kotlinx.serialization.Serializable

@Serializable
data class ScoreBoard(val scores: List<PlayerScore>) {
    init {
        require(scores.sortedDescending() == scores) { "Scores must be sorted highest-to-lowest" }
    }

    @OptIn(ExperimentalStdlibApi::class)
    val ranks: List<Int>
        get() = buildList<Int> {
            add(1)
            scores.zipWithNext { prev, current -> current.compareTo(prev) == 0 }.forEach { exAequoWithPrev ->
                add(if (exAequoWithPrev) last() else size + 1)
            }
        }
}

@Serializable
data class PlayerScore(
    val playerIndex: Int,
    val boardGold: Int,
    val pointsByCategory: Map<ScoreCategory, Int>,
) : Comparable<PlayerScore> {

    val totalPoints = pointsByCategory.values.sum()

    override fun compareTo(other: PlayerScore) = compareValuesBy(this, other, { it.totalPoints }, { it.boardGold })
}

enum class ScoreCategory(val title: String) {
    CIVIL("Civil"),
    SCIENCE("Science"),
    MILITARY("War"),
    TRADE("Trade"),
    GUILD("Guild"),
    WONDER("Wonder"),
    GOLD("Gold")
}

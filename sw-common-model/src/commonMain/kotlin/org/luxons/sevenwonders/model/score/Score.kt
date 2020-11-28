package org.luxons.sevenwonders.model.score

import kotlinx.serialization.Serializable

@Serializable
data class ScoreBoard(val scores: List<PlayerScore>) {

    @OptIn(ExperimentalStdlibApi::class)
    val ranks: List<Int>
        get() = buildList {
            var r = 1
            add(1)
            for (i in 1..scores.lastIndex) {
                if (scores[i] < scores[i - 1]) {
                    add(++r)
                } else {
                    add(r)
                }
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

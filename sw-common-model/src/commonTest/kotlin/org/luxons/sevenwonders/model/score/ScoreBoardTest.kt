package org.luxons.sevenwonders.model.score

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreBoardTest {

    @Test
    fun ranks_simpleOrder() {
        val playerScores = listOf(
            PlayerScore(1, 4, mapOf(ScoreCategory.CIVIL to 2, ScoreCategory.GOLD to 1)),
            PlayerScore(2, 4, mapOf(ScoreCategory.CIVIL to 5, ScoreCategory.GOLD to 1)),
            PlayerScore(3, 4, mapOf(ScoreCategory.SCIENCE to 4, ScoreCategory.GOLD to 1)),
        )
        val sortedScores = playerScores.sortedDescending()
        val scoreBoard = ScoreBoard(sortedScores)
        assertEquals(listOf(2, 3, 1), sortedScores.map { it.playerIndex })
        assertEquals(listOf(1, 2, 3), scoreBoard.ranks)
    }

    @Test
    fun ranks_tiesBrokenByGoldPoints() {
        val playerScores = listOf(
            PlayerScore(1, 4, mapOf(ScoreCategory.CIVIL to 2, ScoreCategory.GOLD to 1)),
            PlayerScore(2, 9, mapOf(ScoreCategory.CIVIL to 5, ScoreCategory.GOLD to 3)),
            PlayerScore(3, 4, mapOf(ScoreCategory.SCIENCE to 4, ScoreCategory.GOLD to 1)),
            PlayerScore(4, 4, mapOf(ScoreCategory.SCIENCE to 7, ScoreCategory.GOLD to 1)),
        )
        val sortedScores = playerScores.sortedDescending()
        val scoreBoard = ScoreBoard(sortedScores)
        assertEquals(listOf(2, 4, 3, 1), sortedScores.map { it.playerIndex })
        assertEquals(listOf(1, 2, 3, 4), scoreBoard.ranks)
    }

    @Test
    fun ranks_tiesBrokenByGoldAmount() {
        val playerScores = listOf(
            PlayerScore(1, 4, mapOf(ScoreCategory.CIVIL to 2, ScoreCategory.GOLD to 1)),
            PlayerScore(2, 6, mapOf(ScoreCategory.CIVIL to 5, ScoreCategory.GOLD to 2)),
            PlayerScore(3, 4, mapOf(ScoreCategory.SCIENCE to 12, ScoreCategory.GOLD to 1)),
            PlayerScore(4, 8, mapOf(ScoreCategory.SCIENCE to 5, ScoreCategory.GOLD to 2)),
        )
        val sortedScores = playerScores.sortedDescending()
        val scoreBoard = ScoreBoard(sortedScores)
        assertEquals(listOf(3, 4, 2, 1), sortedScores.map { it.playerIndex })
        assertEquals(listOf(1, 2, 3, 4), scoreBoard.ranks)
    }

    @Test
    fun ranks_actualTie() {
        val playerScores = listOf(
            PlayerScore(1, 4, mapOf(ScoreCategory.CIVIL to 2, ScoreCategory.GOLD to 1)),
            PlayerScore(2, 6, mapOf(ScoreCategory.CIVIL to 5, ScoreCategory.GOLD to 2)),
            PlayerScore(3, 4, mapOf(ScoreCategory.SCIENCE to 12, ScoreCategory.GOLD to 1)),
            PlayerScore(4, 6, mapOf(ScoreCategory.SCIENCE to 5, ScoreCategory.GOLD to 2)),
        )
        val sortedScores = playerScores.sortedDescending()
        val scoreBoard = ScoreBoard(sortedScores)
        assertEquals(listOf(3, 2, 4, 1), sortedScores.map { it.playerIndex })
        assertEquals(listOf(1, 2, 2, 4), scoreBoard.ranks)
    }
}

package org.luxons.sevenwonders.game.effects

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.boards.BoardElementType
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition
import org.luxons.sevenwonders.game.cards.Card
import org.luxons.sevenwonders.game.cards.Color
import org.luxons.sevenwonders.game.test.createGuildCard
import org.luxons.sevenwonders.game.test.testTable

@RunWith(Theories::class)
class SpecialAbilityActivationTest {

    @Theory
    fun apply_addsAbility(ability: SpecialAbility) {
        val effect = SpecialAbilityActivation(ability)
        val table = testTable(5)

        effect.apply(table, 0)

        val board = table.getBoard(0)
        assertTrue(board.hasSpecial(ability))
    }

    @Theory
    fun computePoints_zeroExceptForCopyGuild(ability: SpecialAbility) {
        Assume.assumeTrue(ability !== SpecialAbility.COPY_GUILD)

        val effect = SpecialAbilityActivation(ability)
        val table = testTable(5)

        assertEquals(0, effect.computePoints(table, 0).toLong())
    }

    @Theory
    fun computePoints_copiedGuild(guildCard: Card, neighbour: RelativeBoardPosition) {
        val effect = SpecialAbilityActivation(SpecialAbility.COPY_GUILD)
        val table = testTable(5)

        val neighbourBoard = table.getBoard(0, neighbour)
        neighbourBoard.addCard(guildCard)

        val board = table.getBoard(0)
        board.copiedGuild = guildCard

        val directPointsFromGuildCard = guildCard.effects.stream().mapToInt { e -> e.computePoints(table, 0) }.sum()
        assertEquals(directPointsFromGuildCard.toLong(), effect.computePoints(table, 0).toLong())
    }

    @Test(expected = IllegalStateException::class)
    fun computePoints_copyGuild_failWhenNoChosenGuild() {
        val effect = SpecialAbilityActivation(SpecialAbility.COPY_GUILD)
        val table = testTable(5)
        effect.computePoints(table, 0)
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun abilities(): Array<SpecialAbility> {
            return SpecialAbility.values()
        }

        @JvmStatic
        @DataPoints
        fun neighbours(): Array<RelativeBoardPosition> {
            return arrayOf(RelativeBoardPosition.LEFT, RelativeBoardPosition.RIGHT)
        }

        @JvmStatic
        @DataPoints
        fun guilds(): Array<Card> {
            val bonus = BonusPerBoardElement(
                listOf(RelativeBoardPosition.LEFT, RelativeBoardPosition.RIGHT),
                BoardElementType.CARD,
                points = 1,
                colors = listOf(Color.GREY, Color.BROWN)
            )
            val bonus2 = BonusPerBoardElement(
                listOf(RelativeBoardPosition.LEFT, RelativeBoardPosition.SELF, RelativeBoardPosition.RIGHT),
                BoardElementType.BUILT_WONDER_STAGES,
                points = 1
            )
            return arrayOf(createGuildCard(1, bonus), createGuildCard(2, bonus2))
        }
    }
}

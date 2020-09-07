package org.luxons.sevenwonders.engine.effects

import org.junit.Assume
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.engine.cards.Card
import org.luxons.sevenwonders.engine.test.createGuildCard
import org.luxons.sevenwonders.engine.test.testTable
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.model.cards.Color
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@RunWith(Theories::class)
class SpecialAbilityActivationTest {

    @Theory
    fun apply_addsAbility(ability: SpecialAbility) {
        val effect = SpecialAbilityActivation(ability)
        val player = SimplePlayer(0, testTable(5))

        effect.applyTo(player)

        assertTrue(player.board.hasSpecial(ability))
    }

    @Theory
    fun computePoints_zeroExceptForCopyGuild(ability: SpecialAbility) {
        Assume.assumeTrue(ability !== SpecialAbility.COPY_GUILD)

        val effect = SpecialAbilityActivation(ability)
        val player = SimplePlayer(0, testTable(5))

        assertEquals(0, effect.computePoints(player))
    }

    @Theory
    internal fun computePoints_copiedGuild(guildCard: Card, neighbour: RelativeBoardPosition) {
        val effect = SpecialAbilityActivation(SpecialAbility.COPY_GUILD)
        val player = SimplePlayer(0, testTable(5))

        val neighbourBoard = player.getBoard(neighbour)
        neighbourBoard.addCard(guildCard)

        player.board.copiedGuild = guildCard

        val directPointsFromGuildCard = guildCard.effects.sumBy { it.computePoints(player) }
        assertEquals(directPointsFromGuildCard, effect.computePoints(player))
    }

    @Test
    fun computePoints_copyGuild_failWhenNoChosenGuild() {
        val effect = SpecialAbilityActivation(SpecialAbility.COPY_GUILD)
        val player = SimplePlayer(0, testTable(5))
        assertFailsWith<IllegalStateException> {
            effect.computePoints(player)
        }
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun abilities(): Array<SpecialAbility> = SpecialAbility.values()

        @JvmStatic
        @DataPoints
        fun neighbours(): Array<RelativeBoardPosition> =
            arrayOf(RelativeBoardPosition.LEFT, RelativeBoardPosition.RIGHT)

        @JvmStatic
        @DataPoints
        internal fun guilds(): Array<Card> {
            val bonus = BonusPerBoardElement(
                boards = listOf(RelativeBoardPosition.LEFT, RelativeBoardPosition.RIGHT),
                type = BoardElementType.CARD,
                points = 1,
                colors = listOf(Color.GREY, Color.BROWN),
            )
            val bonus2 = BonusPerBoardElement(
                boards = listOf(RelativeBoardPosition.LEFT, RelativeBoardPosition.SELF, RelativeBoardPosition.RIGHT),
                type = BoardElementType.BUILT_WONDER_STAGES,
                points = 1,
            )
            return arrayOf(createGuildCard(1, bonus), createGuildCard(2, bonus2))
        }
    }
}

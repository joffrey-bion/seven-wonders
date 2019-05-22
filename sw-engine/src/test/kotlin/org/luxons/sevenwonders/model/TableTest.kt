package org.luxons.sevenwonders.model

import org.junit.Assume.assumeTrue
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.model.boards.RelativeBoardPosition
import org.luxons.sevenwonders.engine.test.createGuildCards
import org.luxons.sevenwonders.engine.test.testTable
import kotlin.test.assertEquals

@RunWith(Theories::class)
class TableTest {

    @Theory
    fun getBoard_wrapLeft(nbPlayers: Int) {
        assumeTrue(nbPlayers >= 2)
        val table = testTable(nbPlayers)
        val last = nbPlayers - 1
        assertEquals(table.getBoard(last), table.getBoard(0, RelativeBoardPosition.LEFT))
        assertEquals(table.getBoard(0), table.getBoard(0, RelativeBoardPosition.SELF))
        assertEquals(table.getBoard(1), table.getBoard(0, RelativeBoardPosition.RIGHT))
    }

    @Theory
    fun getBoard_wrapRight(nbPlayers: Int) {
        assumeTrue(nbPlayers >= 2)
        val table = testTable(nbPlayers)
        val last = nbPlayers - 1
        assertEquals(table.getBoard(last - 1), table.getBoard(last, RelativeBoardPosition.LEFT))
        assertEquals(table.getBoard(last), table.getBoard(last, RelativeBoardPosition.SELF))
        assertEquals(table.getBoard(0), table.getBoard(last, RelativeBoardPosition.RIGHT))
    }

    @Theory
    fun getBoard_noWrap(nbPlayers: Int) {
        assumeTrue(nbPlayers >= 3)
        val table = testTable(nbPlayers)
        assertEquals(table.getBoard(0), table.getBoard(1, RelativeBoardPosition.LEFT))
        assertEquals(table.getBoard(1), table.getBoard(1, RelativeBoardPosition.SELF))
        assertEquals(table.getBoard(2), table.getBoard(1, RelativeBoardPosition.RIGHT))
    }

    @Theory
    fun getNeighbourGuildCards(nbPlayers: Int) {
        assumeTrue(nbPlayers >= 4)
        val table = testTable(nbPlayers)
        val guildCards = createGuildCards(4)
        table.getBoard(0).addCard(guildCards[0])
        table.getBoard(0).addCard(guildCards[1])
        table.getBoard(1).addCard(guildCards[2])
        table.getBoard(2).addCard(guildCards[3])

        val neighbourCards0 = table.getNeighbourGuildCards(0)
        assertEquals(listOf(guildCards[2]), neighbourCards0)

        val neighbourCards1 = table.getNeighbourGuildCards(1)
        assertEquals(guildCards - guildCards[2], neighbourCards1)

        val neighbourCards2 = table.getNeighbourGuildCards(2)
        assertEquals(listOf(guildCards[2]), neighbourCards2)
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun nbPlayers(): IntArray = intArrayOf(2, 3, 4, 5, 6, 7, 8)
    }
}

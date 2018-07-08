package org.luxons.sevenwonders.game.api

import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.boards.RelativeBoardPosition
import org.luxons.sevenwonders.game.test.createGuildCards
import org.luxons.sevenwonders.game.test.testTable

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
        table.getBoard(0).playedCards.add(guildCards[0])
        table.getBoard(0).playedCards.add(guildCards[1])
        table.getBoard(1).playedCards.add(guildCards[2])
        table.getBoard(2).playedCards.add(guildCards[3])

        val neightbourCards0 = table.getNeighbourGuildCards(0)
        assertEquals(1, neightbourCards0.size.toLong())
        assertFalse(neightbourCards0.contains(guildCards[0]))
        assertFalse(neightbourCards0.contains(guildCards[1]))
        assertTrue(neightbourCards0.contains(guildCards[2]))
        assertFalse(neightbourCards0.contains(guildCards[3]))

        val neightbourCards1 = table.getNeighbourGuildCards(1)
        assertEquals(3, neightbourCards1.size.toLong())
        assertTrue(neightbourCards1.contains(guildCards[0]))
        assertTrue(neightbourCards1.contains(guildCards[1]))
        assertFalse(neightbourCards1.contains(guildCards[2]))
        assertTrue(neightbourCards1.contains(guildCards[3]))

        val neightbourCards2 = table.getNeighbourGuildCards(2)
        assertEquals(1, neightbourCards2.size.toLong())
        assertFalse(neightbourCards2.contains(guildCards[0]))
        assertFalse(neightbourCards2.contains(guildCards[1]))
        assertTrue(neightbourCards2.contains(guildCards[2]))
        assertFalse(neightbourCards2.contains(guildCards[3]))
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun nbPlayers(): IntArray {
            return intArrayOf(2, 3, 4, 5, 6, 7, 8)
        }
    }
}

package org.luxons.sevenwonders.game.boards

import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith

import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue

@RunWith(Theories::class)
class RelativeBoardPositionTest {

    @Theory
    fun getIndexFrom_wrapLeft(nbPlayers: Int) {
        assumeTrue(nbPlayers >= 2)
        val last = nbPlayers - 1
        assertEquals(last.toLong(), RelativeBoardPosition.LEFT.getIndexFrom(0, nbPlayers).toLong())
        assertEquals(0, RelativeBoardPosition.SELF.getIndexFrom(0, nbPlayers).toLong())
        assertEquals(1, RelativeBoardPosition.RIGHT.getIndexFrom(0, nbPlayers).toLong())
    }

    @Theory
    fun getIndexFrom_wrapRight(nbPlayers: Int) {
        assumeTrue(nbPlayers >= 2)
        val last = nbPlayers - 1
        assertEquals((last - 1).toLong(), RelativeBoardPosition.LEFT.getIndexFrom(last, nbPlayers).toLong())
        assertEquals(last.toLong(), RelativeBoardPosition.SELF.getIndexFrom(last, nbPlayers).toLong())
        assertEquals(0, RelativeBoardPosition.RIGHT.getIndexFrom(last, nbPlayers).toLong())
    }

    @Theory
    fun getIndexFrom_noWrap(nbPlayers: Int) {
        assumeTrue(nbPlayers >= 3)
        assertEquals(0, RelativeBoardPosition.LEFT.getIndexFrom(1, nbPlayers).toLong())
        assertEquals(1, RelativeBoardPosition.SELF.getIndexFrom(1, nbPlayers).toLong())
        assertEquals(2, RelativeBoardPosition.RIGHT.getIndexFrom(1, nbPlayers).toLong())
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun nbPlayers(): IntArray {
            return intArrayOf(1, 2, 3, 5, 7, 9)
        }
    }
}

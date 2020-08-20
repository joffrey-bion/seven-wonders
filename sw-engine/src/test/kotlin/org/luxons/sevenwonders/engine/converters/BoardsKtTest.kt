package org.luxons.sevenwonders.engine.converters

import org.luxons.sevenwonders.engine.test.testCard
import org.luxons.sevenwonders.model.cards.Color
import org.luxons.sevenwonders.model.cards.TableCard
import kotlin.test.Test
import kotlin.test.assertEquals

class BoardsKtTest {

    @Test
    fun `toColumns on empty list should return no cols`() {
        val cols = emptyList<TableCard>().toColumns()
        assertEquals(emptyList(), cols)
    }

    @Test
    fun `toColumns with single resource should return a single column`() {
        val card = testCard(color = Color.BROWN).toTableCard()
        val cols = listOf(card).toColumns()
        assertEquals(listOf(listOf(card)), cols)
    }

    @Test
    fun `toColumns with single non-resource should return a single column`() {
        val card = testCard(color = Color.BLUE).toTableCard()
        val cols = listOf(card).toColumns()
        assertEquals(listOf(listOf(card)), cols)
    }

    @Test
    fun `toColumns with two same-color cards should return a single column`() {
        val card1 = testCard(color = Color.BLUE).toTableCard()
        val card2 = testCard(color = Color.BLUE).toTableCard()
        val cards = listOf(card1, card2)
        val cols = cards.toColumns()
        assertEquals(listOf(cards), cols)
    }

    @Test
    fun `toColumns with two resource cards should return a single column`() {
        val card1 = testCard(color = Color.BROWN).toTableCard()
        val card2 = testCard(color = Color.GREY).toTableCard()
        val cards = listOf(card1, card2)
        val cols = cards.toColumns()
        assertEquals(listOf(cards), cols)
    }

    @Test
    fun `toColumns with 2 different non-resource cards should return 2 columns`() {
        val card1 = testCard(color = Color.BLUE).toTableCard()
        val card2 = testCard(color = Color.GREEN).toTableCard()
        val cards = listOf(card1, card2)
        val cols = cards.toColumns()
        assertEquals(listOf(listOf(card1), listOf(card2)), cols)
    }

    @Test
    fun `toColumns with 1 res and 1 non-res card should return 2 columns`() {
        val card1 = testCard(color = Color.BROWN).toTableCard()
        val card2 = testCard(color = Color.GREEN).toTableCard()
        val cards = listOf(card1, card2)
        val cols = cards.toColumns()
        assertEquals(listOf(listOf(card1), listOf(card2)), cols)
    }

    @Test
    fun `toColumns should return 1 col for res cards and 1 for each other color`() {
        val res1 = testCard(color = Color.BROWN).toTableCard()
        val res2 = testCard(color = Color.BROWN).toTableCard()
        val res3 = testCard(color = Color.GREY).toTableCard()
        val blue1 = testCard(color = Color.BLUE).toTableCard()
        val green1 = testCard(color = Color.GREEN).toTableCard()
        val green2 = testCard(color = Color.GREEN).toTableCard()
        val cards = listOf(res1, green1, green2, res2, blue1, res3)
        val cols = cards.toColumns()
        val expectedCols = listOf(
            listOf(res1, res2, res3),
            listOf(blue1),
            listOf(green1, green2)
        )
        assertEquals(expectedCols, cols)
    }
}

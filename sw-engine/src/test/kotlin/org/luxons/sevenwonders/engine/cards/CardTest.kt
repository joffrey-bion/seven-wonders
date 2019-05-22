package org.luxons.sevenwonders.engine.cards

import org.junit.Test
import org.luxons.sevenwonders.engine.SimplePlayer
import org.luxons.sevenwonders.model.cards.Color
import org.luxons.sevenwonders.engine.boards.Board
import org.luxons.sevenwonders.engine.boards.Table
import org.luxons.sevenwonders.engine.effects.ProductionIncrease
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.resources.noTransactions
import org.luxons.sevenwonders.engine.test.testCard
import org.luxons.sevenwonders.engine.test.testSettings
import org.luxons.sevenwonders.engine.wonders.Wonder
import kotlin.test.assertEquals

class CardTest {

    @Test
    fun playCardCostingMoney() {
        val initialGold = 3
        val price = 1
        val settings = testSettings(3, initialGold)

        val boards = listOf(
            Board(Wonder("TestWonder", ResourceType.WOOD, emptyList(), ""), 0, settings),
            Board(Wonder("TestWonder", ResourceType.STONE, emptyList(), ""), 1, settings),
            Board(Wonder("TestWonder", ResourceType.PAPYRUS, emptyList(), ""), 2, settings)
        )
        val table = Table(boards)

        val treeFarmRequirements = Requirements(gold = price)
        val treeFarmProduction = Production().apply { addChoice(ResourceType.WOOD, ResourceType.CLAY) }
        val treeFarmEffect = ProductionIncrease(treeFarmProduction, false)
        val treeFarmCard = testCard("Tree Farm", Color.BROWN, treeFarmRequirements, treeFarmEffect)

        treeFarmCard.applyTo(SimplePlayer(0, table), noTransactions())

        assertEquals(initialGold - price, table.getBoard(0).gold)
        assertEquals(initialGold, table.getBoard(1).gold)
        assertEquals(initialGold, table.getBoard(2).gold)
    }
}

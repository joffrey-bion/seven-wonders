package org.luxons.sevenwonders.game.cards

import org.junit.Test
import org.luxons.sevenwonders.game.SimplePlayer
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.effects.ProductionIncrease
import org.luxons.sevenwonders.game.resources.Production
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.noTransactions
import org.luxons.sevenwonders.game.test.testCard
import org.luxons.sevenwonders.game.test.testSettings
import org.luxons.sevenwonders.game.wonders.Wonder
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

package org.luxons.sevenwonders.game.cards

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.Settings
import org.luxons.sevenwonders.game.api.Table
import org.luxons.sevenwonders.game.boards.Board
import org.luxons.sevenwonders.game.effects.ProductionIncrease
import org.luxons.sevenwonders.game.resources.ResourceTransactions
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources
import org.luxons.sevenwonders.game.test.testCard
import org.luxons.sevenwonders.game.wonders.Wonder

class CardTest {

    private var table: Table? = null

    private var treeFarmCard: Card? = null

    @Before
    fun initBoard() {
        val settings = Settings(3)

        val boards = listOf(
            Board(Wonder("TestWonder", ResourceType.WOOD), 0, settings),
            Board(Wonder("TestWonder", ResourceType.STONE), 1, settings),
            Board(Wonder("TestWonder", ResourceType.PAPYRUS), 2, settings)
        )
        table = Table(boards)

        val treeFarmRequirements = Requirements(1)
        val treeFarmEffect = ProductionIncrease()
        treeFarmEffect.production.addChoice(ResourceType.WOOD, ResourceType.CLAY)

        treeFarmCard = testCard("Tree Farm", Color.BROWN, treeFarmEffect, treeFarmRequirements)
    }

    @Test
    fun playCardCostingMoney() {
        table!!.getBoard(0).gold = 3
        table!!.getBoard(1).gold = 3
        table!!.getBoard(2).gold = 3
        treeFarmCard!!.applyTo(table!!, 0, ResourceTransactions())
        assertEquals(2, table!!.getBoard(0).gold.toLong())
        assertEquals(3, table!!.getBoard(1).gold.toLong())
        assertEquals(3, table!!.getBoard(2).gold.toLong())
    }
}

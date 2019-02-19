package org.luxons.sevenwonders.game.resources

import org.junit.Assert.assertEquals
import org.junit.Test
import org.luxons.sevenwonders.game.resources.ResourceType.CLAY
import org.luxons.sevenwonders.game.resources.ResourceType.WOOD
import org.luxons.sevenwonders.game.test.createTransaction

class ResourceTransactionsTest {

    @Test
    fun toTransactions() {
        val transactionMap = mapOf(
            Provider.LEFT_PLAYER to (1 of WOOD) + (1 of CLAY),
            Provider.RIGHT_PLAYER to (1 of WOOD)
        )

        val expectedNormalized = setOf(
            createTransaction(Provider.LEFT_PLAYER, WOOD, CLAY),
            createTransaction(Provider.RIGHT_PLAYER, WOOD)
        )

        assertEquals(expectedNormalized, transactionMap.toTransactions().toSet())
    }

    private infix fun Int.of(type: ResourceType): Resources = resourcesOf(type to this)
}

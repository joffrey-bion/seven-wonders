package org.luxons.sevenwonders.engine.resources

import org.junit.Test
import org.luxons.sevenwonders.model.resources.Provider
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.resources.ResourceType.CLAY
import org.luxons.sevenwonders.model.resources.ResourceType.WOOD
import org.luxons.sevenwonders.engine.test.createTransaction
import kotlin.test.assertEquals

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

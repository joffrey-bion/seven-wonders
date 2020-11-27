package org.luxons.sevenwonders.engine.resources

import org.junit.Test
import org.luxons.sevenwonders.engine.test.createPricedTransaction
import org.luxons.sevenwonders.engine.test.createPricedTransactions
import org.luxons.sevenwonders.model.resources.Provider
import org.luxons.sevenwonders.model.resources.ResourceType
import org.luxons.sevenwonders.model.resources.ResourceType.CLAY
import org.luxons.sevenwonders.model.resources.ResourceType.WOOD
import kotlin.test.assertEquals

class ResourceTransactionsTest {

    @Test
    fun toTransactions() {
        val transactionMap = mapOf(
            Provider.LEFT_PLAYER to (1 of WOOD) + (1 of CLAY),
            Provider.RIGHT_PLAYER to (1 of WOOD),
        )
        val priceMap = mapOf(
            Provider.LEFT_PLAYER to 4,
            Provider.RIGHT_PLAYER to 2,
        )

        val expectedNormalized = createPricedTransactions(
            createPricedTransaction(Provider.LEFT_PLAYER, 4, WOOD, CLAY),
            createPricedTransaction(Provider.RIGHT_PLAYER, 2, WOOD),
        )
        assertEquals(expectedNormalized, transactionMap.toTransactions(priceMap))
    }

    private infix fun Int.of(type: ResourceType): Resources = resourcesOf(type to this)
}

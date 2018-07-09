package org.luxons.sevenwonders.game.resources

import org.junit.Assert.assertEquals
import org.junit.Test
import org.luxons.sevenwonders.game.test.createTransaction

class ResourceTransactionsTest {

    @Test
    fun toTransactions() {
        val transactionList = listOf(
            createTransaction(Provider.LEFT_PLAYER, ResourceType.WOOD),
            createTransaction(Provider.LEFT_PLAYER, ResourceType.CLAY),
            createTransaction(Provider.RIGHT_PLAYER, ResourceType.WOOD)
        )

        val transactions = ResourceTransactions(transactionList)

        val expectedNormalized = setOf(
            createTransaction(Provider.LEFT_PLAYER, ResourceType.WOOD, ResourceType.CLAY),
            createTransaction(Provider.RIGHT_PLAYER, ResourceType.WOOD)
        )

        assertEquals(expectedNormalized, transactions.asList().toSet())
    }

    @Test(expected = IllegalStateException::class)
    fun remove_failsIfNoResourceForThatProvider() {
        val transactions = ResourceTransactions()
        transactions.remove(Provider.LEFT_PLAYER, Resources(ResourceType.WOOD))
    }
}

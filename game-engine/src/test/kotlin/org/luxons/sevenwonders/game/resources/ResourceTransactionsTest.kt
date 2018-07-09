package org.luxons.sevenwonders.game.resources

import java.util.ArrayList
import java.util.HashSet

import org.junit.Test
import org.luxons.sevenwonders.game.test.*

import org.junit.Assert.assertEquals

class ResourceTransactionsTest {

    @Test
    fun toTransactions() {
        val transactionList = ArrayList<ResourceTransaction>()
        transactionList.add(createTransaction(Provider.LEFT_PLAYER, ResourceType.WOOD))
        transactionList.add(createTransaction(Provider.LEFT_PLAYER, ResourceType.CLAY))
        transactionList.add(createTransaction(Provider.RIGHT_PLAYER, ResourceType.WOOD))

        val transactions = ResourceTransactions(transactionList)

        val expectedNormalized = HashSet<ResourceTransaction>()
        expectedNormalized.add(
            createTransaction(Provider.LEFT_PLAYER, ResourceType.WOOD, ResourceType.CLAY)
        )
        expectedNormalized.add(createTransaction(Provider.RIGHT_PLAYER, ResourceType.WOOD))

        assertEquals(expectedNormalized, HashSet(transactions.toTransactions()))
    }

    @Test(expected = IllegalStateException::class)
    fun remove_failsIfNoResourceForThatProvider() {
        val transactions = ResourceTransactions()
        transactions.remove(Provider.LEFT_PLAYER, Resources.of(ResourceType.WOOD))
    }
}

package org.luxons.sevenwonders.game.resources;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;

public class ResourceTransactionsTest {

    @Test
    public void toTransactions() {
        List<ResourceTransaction> transactionList = new ArrayList<>();
        transactionList.add(TestUtilsKt.createTransaction(Provider.LEFT_PLAYER, ResourceType.WOOD));
        transactionList.add(TestUtilsKt.createTransaction(Provider.LEFT_PLAYER, ResourceType.CLAY));
        transactionList.add(TestUtilsKt.createTransaction(Provider.RIGHT_PLAYER, ResourceType.WOOD));

        ResourceTransactions transactions = new ResourceTransactions(transactionList);

        Set<ResourceTransaction> expectedNormalized = new HashSet<>();
        expectedNormalized.add(
                TestUtilsKt.createTransaction(Provider.LEFT_PLAYER, ResourceType.WOOD, ResourceType.CLAY));
        expectedNormalized.add(TestUtilsKt.createTransaction(Provider.RIGHT_PLAYER, ResourceType.WOOD));

        assertEquals(expectedNormalized, new HashSet<>(transactions.toTransactions()));
    }

    @Test(expected = IllegalStateException.class)
    public void remove_failsIfNoResourceForThatProvider() {
        ResourceTransactions transactions = new ResourceTransactions();
        transactions.remove(Provider.LEFT_PLAYER, Resources.of(ResourceType.WOOD));
    }
}

package org.luxons.sevenwonders.game.cards;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.Provider;
import org.luxons.sevenwonders.game.resources.ResourceTransactions;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class RequirementsTest {

    @DataPoints
    public static int[] goldAmounts() {
        return new int[] {0, 1, 2, 5};
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @Test
    public void getResources_emptyAfterInit() {
        Requirements requirements = new Requirements();
        assertTrue(requirements.getResources().isEmpty());
    }

    @Test
    public void setResources_success() {
        Requirements requirements = new Requirements();
        Resources resources = new Resources();
        requirements.setResources(resources);
        assertSame(resources, requirements.getResources());
    }

    @Theory
    public void goldRequirement(int boardGold, int requiredGold) {
        Requirements requirements = new Requirements();
        requirements.setGold(requiredGold);

        Board board = TestUtilsKt.testBoard(ResourceType.CLAY, boardGold);
        Table table = new Table(Collections.singletonList(board));

        assertEquals(boardGold >= requiredGold, requirements.areMetWithoutNeighboursBy(board));
        assertEquals(boardGold >= requiredGold, requirements.areMetWithHelpBy(board, new ResourceTransactions()));
        assertEquals(boardGold >= requiredGold, requirements.areMetBy(table, 0));
    }

    @Theory
    public void resourceRequirement_initialResource(ResourceType initialResource, ResourceType requiredResource) {
        Requirements requirements = TestUtilsKt.createRequirements(requiredResource);

        Board board = TestUtilsKt.testBoard(initialResource, 0);
        Table table = new Table(Collections.singletonList(board));

        assertEquals(initialResource == requiredResource, requirements.areMetWithoutNeighboursBy(board));
        assertEquals(initialResource == requiredResource,
                requirements.areMetWithHelpBy(board, new ResourceTransactions()));

        if (initialResource == requiredResource) {
            assertTrue(requirements.areMetBy(table, 0));
        }
    }

    @Theory
    public void resourceRequirement_ownProduction(ResourceType initialResource, ResourceType producedResource,
            ResourceType requiredResource) {
        assumeTrue(initialResource != requiredResource);

        Requirements requirements = TestUtilsKt.createRequirements(requiredResource);

        Board board = TestUtilsKt.testBoard(initialResource, 0);
        board.getProduction().addFixedResource(producedResource, 1);
        Table table = new Table(Collections.singletonList(board));

        assertEquals(producedResource == requiredResource, requirements.areMetWithoutNeighboursBy(board));
        assertEquals(producedResource == requiredResource,
                requirements.areMetWithHelpBy(board, new ResourceTransactions()));

        if (producedResource == requiredResource) {
            assertTrue(requirements.areMetBy(table, 0));
        }
    }

    @Theory
    public void resourceRequirement_boughtResource(ResourceType initialResource, ResourceType boughtResource,
            ResourceType requiredResource) {
        assumeTrue(initialResource != requiredResource);

        Requirements requirements = TestUtilsKt.createRequirements(requiredResource);

        Board board = TestUtilsKt.testBoard(initialResource, 2);
        Board neighbourBoard = TestUtilsKt.testBoard(initialResource, 0);
        neighbourBoard.getPublicProduction().addFixedResource(boughtResource, 1);
        Table table = new Table(Arrays.asList(board, neighbourBoard));

        ResourceTransactions resources = TestUtilsKt.createTransactions(Provider.RIGHT_PLAYER, boughtResource);

        assertFalse(requirements.areMetWithoutNeighboursBy(board));
        assertEquals(boughtResource == requiredResource, requirements.areMetWithHelpBy(board, resources));

        if (boughtResource == requiredResource) {
            assertTrue(requirements.areMetBy(table, 0));
        }
    }

    @Theory
    public void pay_boughtResource(ResourceType initialResource, ResourceType requiredResource) {
        assumeTrue(initialResource != requiredResource);

        Requirements requirements = TestUtilsKt.createRequirements(requiredResource);

        Board board = TestUtilsKt.testBoard(initialResource, 2);
        Board neighbourBoard = TestUtilsKt.testBoard(requiredResource, 0);
        Table table = new Table(Arrays.asList(board, neighbourBoard));

        ResourceTransactions transactions = TestUtilsKt.createTransactions(Provider.RIGHT_PLAYER,
                requiredResource);

        assertFalse(requirements.areMetWithoutNeighboursBy(board));
        assertTrue(requirements.areMetWithHelpBy(board, transactions));
        assertTrue(requirements.areMetBy(table, 0));

        requirements.pay(table, 0, transactions);

        assertEquals(0, board.getGold());
        assertEquals(2, neighbourBoard.getGold());
    }
}

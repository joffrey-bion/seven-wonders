package org.luxons.sevenwonders.game.cards;

import java.util.Collections;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

@RunWith(Theories.class)
public class RequirementsTest {

    @DataPoints
    public static int[] goldAmounts() {
        return new int[]{0, 1, 2, 5};
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @Theory
    public void goldRequirement(int boardGold, int requiredGold) {
        Requirements requirements = new Requirements();
        requirements.setGold(requiredGold);

        Board board = TestUtils.createBoard(ResourceType.CLAY, boardGold);
        Table table = new Table(Collections.singletonList(board));

        assertEquals(boardGold >= requiredGold, requirements.areMetWithoutNeighboursBy(board));
        assertEquals(boardGold >= requiredGold, requirements.areMetWithHelpBy(board, Collections.emptyList()));
        assertEquals(boardGold >= requiredGold, requirements.couldBeMetBy(table, 0));
    }

    @Theory
    public void resourceRequirement_initialResource(ResourceType initialResource, ResourceType requiredResource) {
        Requirements requirements = TestUtils.createRequirements(requiredResource);

        Board board = TestUtils.createBoard(initialResource, 0);
        Table table = new Table(Collections.singletonList(board));

        assertEquals(initialResource == requiredResource, requirements.areMetWithoutNeighboursBy(board));
        assertEquals(initialResource == requiredResource,
                requirements.areMetWithHelpBy(board, Collections.emptyList()));

        if (initialResource == requiredResource) {
            assertTrue(requirements.couldBeMetBy(table, 0));
        }
    }

    @Theory
    public void resourceRequirement_ownProduction(ResourceType initialResource, ResourceType producedResource,
                                                  ResourceType requiredResource) {
        assumeTrue(initialResource != requiredResource);

        Requirements requirements = TestUtils.createRequirements(requiredResource);

        Board board = TestUtils.createBoard(initialResource, 0);
        board.getProduction().addFixedResource(producedResource, 1);
        Table table = new Table(Collections.singletonList(board));

        assertEquals(producedResource == requiredResource, requirements.areMetWithoutNeighboursBy(board));
        assertEquals(producedResource == requiredResource,
                requirements.areMetWithHelpBy(board, Collections.emptyList()));

        if (producedResource == requiredResource) {
            assertTrue(requirements.couldBeMetBy(table, 0));
        }
    }
}

package org.luxons.sevenwonders.game.effects;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Theories.class)
public class ProductionIncreaseTest {

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    private static ProductionIncrease createProductionIncrease(ResourceType... types) {
        ProductionIncrease effect = new ProductionIncrease();
        effect.getProduction().addAll(TestUtils.createFixedProduction(types));
        return effect;
    }

    @Theory
    public void apply_boardContainsAddedResourceType(ResourceType initialType, ResourceType addedType, ResourceType extraType) {
        Board board = TestUtils.createBoard(initialType);
        ProductionIncrease effect = createProductionIncrease(addedType);

        effect.apply(board, null, null);

        Resources resources = TestUtils.createResources(initialType, addedType);
        assertTrue(board.getProduction().contains(resources));

        Resources moreResources = TestUtils.createResources(initialType, addedType, extraType);
        assertFalse(board.getProduction().contains(moreResources));
    }
}
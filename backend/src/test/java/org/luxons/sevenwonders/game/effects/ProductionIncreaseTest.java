package org.luxons.sevenwonders.game.effects;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.api.Table;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;
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
    public void apply_boardContainsAddedResourceType(ResourceType initialType, ResourceType addedType,
            ResourceType extraType) {
        Board board = TestUtils.createBoard(initialType);
        ProductionIncrease effect = createProductionIncrease(addedType);
        effect.setSellable(false);

        effect.apply(board);

        Resources resources = TestUtils.createResources(initialType, addedType);
        assertTrue(board.getProduction().contains(resources));
        assertFalse(board.getPublicProduction().contains(resources));

        Resources moreResources = TestUtils.createResources(initialType, addedType, extraType);
        assertFalse(board.getProduction().contains(moreResources));
        assertFalse(board.getPublicProduction().contains(moreResources));
    }

    @Theory
    public void apply_boardContainsAddedResourceType_sellable(ResourceType initialType, ResourceType addedType,
            ResourceType extraType) {
        Board board = TestUtils.createBoard(initialType);
        ProductionIncrease effect = createProductionIncrease(addedType);
        effect.setSellable(true);

        effect.apply(board);

        Resources resources = TestUtils.createResources(initialType, addedType);
        assertTrue(board.getProduction().contains(resources));
        assertTrue(board.getPublicProduction().contains(resources));

        Resources moreResources = TestUtils.createResources(initialType, addedType, extraType);
        assertFalse(board.getProduction().contains(moreResources));
        assertFalse(board.getPublicProduction().contains(moreResources));
    }

    @Theory
    public void computePoints_isAlwaysZero(ResourceType addedType) {
        ProductionIncrease effect = createProductionIncrease(addedType);
        Table table = TestUtils.createTable(5);
        assertEquals(0, effect.computePoints(table, 0));
    }

    @Theory
    public void equals_falseWhenNull(ResourceType addedType) {
        ProductionIncrease effect = createProductionIncrease(addedType);
        //noinspection ObjectEqualsNull
        assertFalse(effect.equals(null));
    }

    @Theory
    public void equals_falseWhenDifferentClass(ResourceType addedType) {
        ProductionIncrease effect = createProductionIncrease(addedType);
        Production production = TestUtils.createFixedProduction(addedType);
        //noinspection EqualsBetweenInconvertibleTypes
        assertFalse(effect.equals(production));
    }

    @Theory
    public void equals_trueWhenSame(ResourceType addedType) {
        ProductionIncrease effect = createProductionIncrease(addedType);
        assertEquals(effect, effect);
    }

    @Theory
    public void equals_trueWhenSameContent(ResourceType addedType) {
        ProductionIncrease effect1 = createProductionIncrease(addedType);
        ProductionIncrease effect2 = createProductionIncrease(addedType);
        assertTrue(effect1.equals(effect2));
    }

    @Theory
    public void hashCode_sameWhenSameContent(ResourceType addedType) {
        ProductionIncrease effect1 = createProductionIncrease(addedType);
        ProductionIncrease effect2 = createProductionIncrease(addedType);
        assertEquals(effect1.hashCode(), effect2.hashCode());
    }
}

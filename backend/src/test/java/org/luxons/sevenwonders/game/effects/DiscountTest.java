package org.luxons.sevenwonders.game.effects;

import org.junit.Assume;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.BoughtResources;
import org.luxons.sevenwonders.game.resources.Provider;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.test.TestUtils;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class DiscountTest {

    @DataPoints
    public static int[] discountedPrices() {
        return new int[] {0, 1, 2};
    }

    @DataPoints
    public static ResourceType[] resourceTypes() {
        return ResourceType.values();
    }

    @DataPoints
    public static Provider[] providers() {
        return Provider.values();
    }

    @Theory
    public void apply_givesDiscountedPrice(int discountedPrice, ResourceType discountedType, Provider provider) {
        Board board = TestUtils.createBoard(ResourceType.CLAY, 3);
        Discount discount = new Discount();
        discount.setDiscountedPrice(discountedPrice);
        discount.getProviders().add(provider);
        discount.getResourceTypes().add(discountedType);
        discount.apply(board);

        BoughtResources boughtResources = TestUtils.createBoughtResources(provider, discountedType);
        assertEquals(discountedPrice, board.getTradingRules().computeCost(boughtResources));
    }

    @Theory
    public void apply_doesNotAffectOtherResources(int discountedPrice, ResourceType discountedType, Provider provider,
            ResourceType otherType, Provider otherProvider) {
        Assume.assumeTrue(otherProvider != provider);
        Assume.assumeTrue(otherType != discountedType);

        Board board = TestUtils.createBoard(ResourceType.CLAY, 3);
        Discount discount = new Discount();
        discount.setDiscountedPrice(discountedPrice);
        discount.getProviders().add(provider);
        discount.getResourceTypes().add(discountedType);
        discount.apply(board);

        // this is the default in the settings used by TestUtils.createBoard()
        int normalPrice = 2;

        BoughtResources fromOtherType = TestUtils.createBoughtResources(provider, otherType);
        assertEquals(normalPrice, board.getTradingRules().computeCost(fromOtherType));

        BoughtResources fromOtherProvider = TestUtils.createBoughtResources(otherProvider, discountedType);
        assertEquals(normalPrice, board.getTradingRules().computeCost(fromOtherProvider));

        BoughtResources fromOtherProviderAndType = TestUtils.createBoughtResources(otherProvider, otherType);
        assertEquals(normalPrice, board.getTradingRules().computeCost(fromOtherProviderAndType));
    }
}

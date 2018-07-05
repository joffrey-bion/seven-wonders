package org.luxons.sevenwonders.game.effects;

import org.junit.Assume;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.boards.Board;
import org.luxons.sevenwonders.game.resources.Provider;
import org.luxons.sevenwonders.game.resources.ResourceTransactions;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.test.TestUtilsKt;

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
        Board board = TestUtilsKt.testBoard(ResourceType.CLAY, 3);
        Discount discount = new Discount();
        discount.setDiscountedPrice(discountedPrice);
        discount.getProviders().add(provider);
        discount.getResourceTypes().add(discountedType);
        discount.apply(board);

        ResourceTransactions transactions = TestUtilsKt.createTransactions(provider, discountedType);
        assertEquals(discountedPrice, board.getTradingRules().computeCost(transactions));
    }

    @Theory
    public void apply_doesNotAffectOtherResources(int discountedPrice, ResourceType discountedType, Provider provider,
            ResourceType otherType, Provider otherProvider) {
        Assume.assumeTrue(otherProvider != provider);
        Assume.assumeTrue(otherType != discountedType);

        Board board = TestUtilsKt.testBoard(ResourceType.CLAY, 3);
        Discount discount = new Discount();
        discount.setDiscountedPrice(discountedPrice);
        discount.getProviders().add(provider);
        discount.getResourceTypes().add(discountedType);
        discount.apply(board);

        // this is the default in the settings used by TestUtilsKt.testBoard()
        int normalPrice = 2;

        ResourceTransactions fromOtherType = TestUtilsKt.createTransactions(provider, otherType);
        assertEquals(normalPrice, board.getTradingRules().computeCost(fromOtherType));

        ResourceTransactions fromOtherProvider = TestUtilsKt.createTransactions(otherProvider, discountedType);
        assertEquals(normalPrice, board.getTradingRules().computeCost(fromOtherProvider));

        ResourceTransactions fromOtherProviderAndType = TestUtilsKt.createTransactions(otherProvider, otherType);
        assertEquals(normalPrice, board.getTradingRules().computeCost(fromOtherProviderAndType));
    }
}

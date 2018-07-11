package org.luxons.sevenwonders.game.effects

import org.junit.Assert.assertEquals
import org.junit.Assume
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.resources.Provider
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.test.createTransactions
import org.luxons.sevenwonders.game.test.testBoard

@RunWith(Theories::class)
class DiscountTest {

    @Theory
    fun apply_givesDiscountedPrice(discountedPrice: Int, discountedType: ResourceType, provider: Provider) {
        val board = testBoard(ResourceType.CLAY, 3)
        val discount = Discount(listOf(discountedType), listOf(provider), discountedPrice)
        discount.applyTo(board)

        val transactions = createTransactions(provider, discountedType)
        assertEquals(discountedPrice.toLong(), board.tradingRules.computeCost(transactions).toLong())
    }

    @Theory
    fun apply_doesNotAffectOtherResources(
        discountedPrice: Int, discountedType: ResourceType, provider: Provider,
        otherType: ResourceType, otherProvider: Provider
    ) {
        Assume.assumeTrue(otherProvider != provider)
        Assume.assumeTrue(otherType != discountedType)

        val board = testBoard(ResourceType.CLAY, 3)
        val discount = Discount(listOf(discountedType), listOf(provider), discountedPrice)
        discount.applyTo(board)

        // this is the default in the settings used by TestUtilsKt.testBoard()
        val normalPrice = 2

        val fromOtherType = createTransactions(provider, otherType)
        assertEquals(normalPrice.toLong(), board.tradingRules.computeCost(fromOtherType).toLong())

        val fromOtherProvider = createTransactions(otherProvider, discountedType)
        assertEquals(normalPrice.toLong(), board.tradingRules.computeCost(fromOtherProvider).toLong())

        val fromOtherProviderAndType = createTransactions(otherProvider, otherType)
        assertEquals(normalPrice.toLong(), board.tradingRules.computeCost(fromOtherProviderAndType).toLong())
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun discountedPrices(): IntArray = intArrayOf(0, 1, 2)

        @JvmStatic
        @DataPoints
        fun resourceTypes(): Array<ResourceType> = ResourceType.values()

        @JvmStatic
        @DataPoints
        fun providers(): Array<Provider> = Provider.values()
    }
}

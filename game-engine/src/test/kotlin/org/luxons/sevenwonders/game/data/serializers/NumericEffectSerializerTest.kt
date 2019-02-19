package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import org.luxons.sevenwonders.game.effects.GoldIncrease
import org.luxons.sevenwonders.game.effects.MilitaryReinforcements
import org.luxons.sevenwonders.game.effects.ProductionIncrease
import org.luxons.sevenwonders.game.effects.RawPointsIncrease
import org.luxons.sevenwonders.game.resources.Production

@RunWith(Theories::class)
class NumericEffectSerializerTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = GsonBuilder().registerTypeAdapter(MilitaryReinforcements::class.java, NumericEffectSerializer())
            .registerTypeAdapter(RawPointsIncrease::class.java, NumericEffectSerializer())
            .registerTypeAdapter(GoldIncrease::class.java, NumericEffectSerializer())
            // ProductionIncrease is not a numeric effect, it is here for negative testing purpose
            .registerTypeAdapter(ProductionIncrease::class.java, NumericEffectSerializer())
            .create()
    }

    @Test
    fun serialize_militaryReinforcements_null() {
        assertEquals("null", gson.toJson(null, MilitaryReinforcements::class.java))
    }

    @Test
    fun serialize_rawPointsIncrease_null() {
        assertEquals("null", gson.toJson(null, RawPointsIncrease::class.java))
    }

    @Test
    fun serialize_goldIncrease_null() {
        assertEquals("null", gson.toJson(null, GoldIncrease::class.java))
    }

    @Test(expected = IllegalArgumentException::class)
    fun serialize_failOnUnknownType() {
        gson.toJson(ProductionIncrease(Production(), false))
    }

    @Theory
    fun serialize_militaryReinforcements(count: Int) {
        val reinforcements = MilitaryReinforcements(count)
        assertEquals(count.toString(), gson.toJson(reinforcements))
    }

    @Theory
    fun serialize_rawPointsIncrease(count: Int) {
        val points = RawPointsIncrease(count)
        assertEquals(count.toString(), gson.toJson(points))
    }

    @Theory
    fun serialize_goldIncrease(count: Int) {
        val goldIncrease = GoldIncrease(count)
        assertEquals(count.toString(), gson.toJson(goldIncrease))
    }

    @Theory
    fun deserialize_militaryReinforcements(count: Int) {
        val reinforcements = MilitaryReinforcements(count)
        assertEquals(reinforcements, gson.fromJson(count.toString(), MilitaryReinforcements::class.java))
    }

    @Theory
    fun deserialize_rawPointsIncrease(count: Int) {
        val points = RawPointsIncrease(count)
        assertEquals(points, gson.fromJson(count.toString(), RawPointsIncrease::class.java))
    }

    @Theory
    fun deserialize_goldIncrease(count: Int) {
        val goldIncrease = GoldIncrease(count)
        assertEquals(goldIncrease, gson.fromJson(count.toString(), GoldIncrease::class.java))
    }

    @Test(expected = NumberFormatException::class)
    fun deserialize_militaryReinforcements_failOnEmptyString() {
        gson.fromJson("\"\"", MilitaryReinforcements::class.java)
    }

    @Test(expected = NumberFormatException::class)
    fun deserialize_rawPointsIncrease_failOnEmptyString() {
        gson.fromJson("\"\"", RawPointsIncrease::class.java)
    }

    @Test(expected = NumberFormatException::class)
    fun deserialize_goldIncrease_failOnEmptyString() {
        gson.fromJson("\"\"", GoldIncrease::class.java)
    }

    @Test(expected = NumberFormatException::class)
    fun deserialize_militaryReinforcements_failOnNonNumericString() {
        gson.fromJson("\"abc\"", MilitaryReinforcements::class.java)
    }

    @Test(expected = NumberFormatException::class)
    fun deserialize_rawPointsIncrease_failOnNonNumericString() {
        gson.fromJson("\"abc\"", RawPointsIncrease::class.java)
    }

    @Test(expected = NumberFormatException::class)
    fun deserialize_goldIncrease_failOnNonNumericString() {
        gson.fromJson("\"abc\"", GoldIncrease::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failOnUnknownType() {
        gson.fromJson("\"2\"", ProductionIncrease::class.java)
    }

    companion object {

        @JvmStatic
        @DataPoints
        fun dataPoints(): IntArray = intArrayOf(-2, -1, 0, 1, 2, 5)
    }
}

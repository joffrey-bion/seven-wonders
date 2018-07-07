package org.luxons.sevenwonders.game.data.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.luxons.sevenwonders.game.effects.GoldIncrease;
import org.luxons.sevenwonders.game.effects.MilitaryReinforcements;
import org.luxons.sevenwonders.game.effects.ProductionIncrease;
import org.luxons.sevenwonders.game.effects.RawPointsIncrease;
import org.luxons.sevenwonders.game.resources.Production;

import static org.junit.Assert.assertEquals;

@RunWith(Theories.class)
public class NumericEffectSerializerTest {

    private Gson gson;

    @DataPoints
    public static int[] dataPoints() {
        return new int[] {-2, -1, 0, 1, 2, 5};
    }

    @Before
    public void setUp() {
        gson = new GsonBuilder().registerTypeAdapter(MilitaryReinforcements.class, new NumericEffectSerializer())
                                .registerTypeAdapter(RawPointsIncrease.class, new NumericEffectSerializer())
                                .registerTypeAdapter(GoldIncrease.class, new NumericEffectSerializer())
                                // ProductionIncrease is not a numeric effect, it is here for negative testing purpose
                                .registerTypeAdapter(ProductionIncrease.class, new NumericEffectSerializer())
                                .create();
    }

    @Test
    public void serialize_militaryReinforcements_null() {
        assertEquals("null", gson.toJson(null, MilitaryReinforcements.class));
    }

    @Test
    public void serialize_rawPointsIncrease_null() {
        assertEquals("null", gson.toJson(null, RawPointsIncrease.class));
    }

    @Test
    public void serialize_goldIncrease_null() {
        assertEquals("null", gson.toJson(null, GoldIncrease.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void serialize_failOnUnknownType() {
        gson.toJson(new ProductionIncrease(new Production(), false));
    }

    @Theory
    public void serialize_militaryReinforcements(int count) {
        MilitaryReinforcements reinforcements = new MilitaryReinforcements(count);
        assertEquals(String.valueOf(count), gson.toJson(reinforcements));
    }

    @Theory
    public void serialize_rawPointsIncrease(int count) {
        RawPointsIncrease points = new RawPointsIncrease(count);
        assertEquals(String.valueOf(count), gson.toJson(points));
    }

    @Theory
    public void serialize_goldIncrease(int count) {
        GoldIncrease goldIncrease = new GoldIncrease(count);
        assertEquals(String.valueOf(count), gson.toJson(goldIncrease));
    }

    @Theory
    public void deserialize_militaryReinforcements(int count) {
        MilitaryReinforcements reinforcements = new MilitaryReinforcements(count);
        assertEquals(reinforcements, gson.fromJson(String.valueOf(count), MilitaryReinforcements.class));
    }

    @Theory
    public void deserialize_rawPointsIncrease(int count) {
        RawPointsIncrease points = new RawPointsIncrease(count);
        assertEquals(points, gson.fromJson(String.valueOf(count), RawPointsIncrease.class));
    }

    @Theory
    public void deserialize_goldIncrease(int count) {
        GoldIncrease goldIncrease = new GoldIncrease(count);
        assertEquals(goldIncrease, gson.fromJson(String.valueOf(count), GoldIncrease.class));
    }

    @Test(expected = NumberFormatException.class)
    public void deserialize_militaryReinforcements_failOnEmptyString() {
        gson.fromJson("\"\"", MilitaryReinforcements.class);
    }

    @Test(expected = NumberFormatException.class)
    public void deserialize_rawPointsIncrease_failOnEmptyString() {
        gson.fromJson("\"\"", RawPointsIncrease.class);
    }

    @Test(expected = NumberFormatException.class)
    public void deserialize_goldIncrease_failOnEmptyString() {
        gson.fromJson("\"\"", GoldIncrease.class);
    }

    @Test(expected = NumberFormatException.class)
    public void deserialize_militaryReinforcements_failOnNonNumericString() {
        gson.fromJson("\"abc\"", MilitaryReinforcements.class);
    }

    @Test(expected = NumberFormatException.class)
    public void deserialize_rawPointsIncrease_failOnNonNumericString() {
        gson.fromJson("\"abc\"", RawPointsIncrease.class);
    }

    @Test(expected = NumberFormatException.class)
    public void deserialize_goldIncrease_failOnNonNumericString() {
        gson.fromJson("\"abc\"", GoldIncrease.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnUnknownType() {
        gson.fromJson("\"2\"", ProductionIncrease.class);
    }
}

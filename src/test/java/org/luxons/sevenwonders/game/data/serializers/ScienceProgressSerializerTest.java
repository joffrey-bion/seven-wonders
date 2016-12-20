package org.luxons.sevenwonders.game.data.serializers;

import org.junit.Before;
import org.junit.Test;
import org.luxons.sevenwonders.game.boards.Science;
import org.luxons.sevenwonders.game.boards.ScienceType;
import org.luxons.sevenwonders.game.effects.ScienceProgress;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import static org.junit.Assert.*;

public class ScienceProgressSerializerTest {

    private static final String COMPASS_STR = "\"COMPASS\"";

    private static final String WHEEL_STR = "\"WHEEL\"";

    private static final String TABLET_STR = "\"TABLET\"";

    private static final String JOKER_STR = "\"any\"";

    private Gson gson;

    @Before
    public void setUp() {
        gson = new GsonBuilder().registerTypeAdapter(ScienceProgress.class, new ScienceProgressSerializer()).create();
    }

    private static ScienceProgress create(int compasses, int wheels, int tablets, int jokers) {
        Science science = new Science();
        if (compasses > 0) {
            science.add(ScienceType.COMPASS, compasses);
        }
        if (wheels > 0) {
            science.add(ScienceType.WHEEL, wheels);
        }
        if (tablets > 0) {
            science.add(ScienceType.TABLET, tablets);
        }
        if (jokers > 0) {
            science.addJoker(jokers);
        }
        ScienceProgress progress = new ScienceProgress();
        progress.setScience(science);
        return progress;
    }

    @Test
    public void serialize_emptyToNull() {
        ScienceProgress progress = create(0,0,0,0);
        String json = gson.toJson(progress);
        assertEquals("null", json);
    }

    @Test
    public void serialize_oneCompass() {
        ScienceProgress progress = create(1,0,0,0);
        String json = gson.toJson(progress);
        assertEquals(COMPASS_STR, json);
    }

    @Test
    public void serialize_oneWheel() {
        ScienceProgress progress = create(0,1,0,0);
        String json = gson.toJson(progress);
        assertEquals(WHEEL_STR, json);
    }

    @Test
    public void serialize_oneTablet() {
        ScienceProgress progress = create(0,0,1,0);
        String json = gson.toJson(progress);
        assertEquals(TABLET_STR, json);
    }

    @Test
    public void serialize_oneJoker() {
        ScienceProgress progress = create(0,0,0,1);
        String json = gson.toJson(progress);
        assertEquals(JOKER_STR, json);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void serialize_failOnMultipleCompasses() {
        ScienceProgress progress = create(2,0,0,0);
        gson.toJson(progress);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void serialize_failOnMultipleWheels() {
        ScienceProgress progress = create(0,2,0,0);
        gson.toJson(progress);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void serialize_failOnMultipleTablets() {
        ScienceProgress progress = create(0,0,2,0);
        gson.toJson(progress);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void serialize_failOnMultipleJokers() {
        ScienceProgress progress = create(0,0,0,2);
        gson.toJson(progress);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void serialize_failOnMixedElements() {
        ScienceProgress progress = create(1,1,0,0);
        gson.toJson(progress);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnEmptyString() {
        gson.fromJson("\"\"", ScienceProgress.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deserialize_failOnGarbageString() {
        gson.fromJson("thisisgarbage", ScienceProgress.class);
    }

    @Test
    public void deserialize_compass() {
        ScienceProgress progress = gson.fromJson(COMPASS_STR, ScienceProgress.class);
        assertNotNull(progress.getScience());
        assertEquals(1, progress.getScience().getQuantity(ScienceType.COMPASS));
        assertEquals(0, progress.getScience().getQuantity(ScienceType.WHEEL));
        assertEquals(0, progress.getScience().getQuantity(ScienceType.TABLET));
        assertEquals(0, progress.getScience().getJokers());
    }

    @Test
    public void deserialize_wheel() {
        ScienceProgress progress = gson.fromJson(WHEEL_STR, ScienceProgress.class);
        assertNotNull(progress.getScience());
        assertEquals(0, progress.getScience().getQuantity(ScienceType.COMPASS));
        assertEquals(1, progress.getScience().getQuantity(ScienceType.WHEEL));
        assertEquals(0, progress.getScience().getQuantity(ScienceType.TABLET));
        assertEquals(0, progress.getScience().getJokers());
    }

    @Test
    public void deserialize_tablet() {
        ScienceProgress progress = gson.fromJson(TABLET_STR, ScienceProgress.class);
        assertNotNull(progress.getScience());
        assertEquals(0, progress.getScience().getQuantity(ScienceType.COMPASS));
        assertEquals(0, progress.getScience().getQuantity(ScienceType.WHEEL));
        assertEquals(1, progress.getScience().getQuantity(ScienceType.TABLET));
        assertEquals(0, progress.getScience().getJokers());
    }

    @Test
    public void deserialize_joker() {
        ScienceProgress progress = gson.fromJson(JOKER_STR, ScienceProgress.class);
        assertNotNull(progress.getScience());
        assertEquals(0, progress.getScience().getQuantity(ScienceType.COMPASS));
        assertEquals(0, progress.getScience().getQuantity(ScienceType.WHEEL));
        assertEquals(0, progress.getScience().getQuantity(ScienceType.TABLET));
        assertEquals(1, progress.getScience().getJokers());
    }

}
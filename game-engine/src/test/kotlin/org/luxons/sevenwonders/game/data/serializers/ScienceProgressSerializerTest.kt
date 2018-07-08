package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.boards.ScienceType
import org.luxons.sevenwonders.game.effects.ScienceProgress
import org.luxons.sevenwonders.game.test.createScienceProgress

class ScienceProgressSerializerTest {

    private var gson: Gson? = null

    @Before
    fun setUp() {
        gson = GsonBuilder().registerTypeAdapter(ScienceProgress::class.java, ScienceProgressSerializer()).create()
    }

    @Test
    fun serialize_emptyToNull() {
        val progress = createScienceProgress(0, 0, 0, 0)
        val json = gson!!.toJson(progress)
        assertEquals("null", json)
    }

    @Test
    fun serialize_oneCompass() {
        val progress = createScienceProgress(1, 0, 0, 0)
        val json = gson!!.toJson(progress)
        assertEquals(COMPASS_STR, json)
    }

    @Test
    fun serialize_oneWheel() {
        val progress = createScienceProgress(0, 1, 0, 0)
        val json = gson!!.toJson(progress)
        assertEquals(WHEEL_STR, json)
    }

    @Test
    fun serialize_oneTablet() {
        val progress = createScienceProgress(0, 0, 1, 0)
        val json = gson!!.toJson(progress)
        assertEquals(TABLET_STR, json)
    }

    @Test
    fun serialize_oneJoker() {
        val progress = createScienceProgress(0, 0, 0, 1)
        val json = gson!!.toJson(progress)
        assertEquals(JOKER_STR, json)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun serialize_failOnMultipleCompasses() {
        val progress = createScienceProgress(2, 0, 0, 0)
        gson!!.toJson(progress)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun serialize_failOnMultipleWheels() {
        val progress = createScienceProgress(0, 2, 0, 0)
        gson!!.toJson(progress)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun serialize_failOnMultipleTablets() {
        val progress = createScienceProgress(0, 0, 2, 0)
        gson!!.toJson(progress)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun serialize_failOnMultipleJokers() {
        val progress = createScienceProgress(0, 0, 0, 2)
        gson!!.toJson(progress)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun serialize_failOnMixedElements() {
        val progress = createScienceProgress(1, 1, 0, 0)
        gson!!.toJson(progress)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failOnEmptyString() {
        gson!!.fromJson("\"\"", ScienceProgress::class.java)
    }

    @Test(expected = IllegalArgumentException::class)
    fun deserialize_failOnGarbageString() {
        gson!!.fromJson("thisisgarbage", ScienceProgress::class.java)
    }

    @Test
    fun deserialize_compass() {
        val progress = gson!!.fromJson(COMPASS_STR, ScienceProgress::class.java)
        assertNotNull(progress.science)
        assertEquals(1, progress.science.getQuantity(ScienceType.COMPASS).toLong())
        assertEquals(0, progress.science.getQuantity(ScienceType.WHEEL).toLong())
        assertEquals(0, progress.science.getQuantity(ScienceType.TABLET).toLong())
        assertEquals(0, progress.science.jokers.toLong())
    }

    @Test
    fun deserialize_wheel() {
        val progress = gson!!.fromJson(WHEEL_STR, ScienceProgress::class.java)
        assertNotNull(progress.science)
        assertEquals(0, progress.science.getQuantity(ScienceType.COMPASS).toLong())
        assertEquals(1, progress.science.getQuantity(ScienceType.WHEEL).toLong())
        assertEquals(0, progress.science.getQuantity(ScienceType.TABLET).toLong())
        assertEquals(0, progress.science.jokers.toLong())
    }

    @Test
    fun deserialize_tablet() {
        val progress = gson!!.fromJson(TABLET_STR, ScienceProgress::class.java)
        assertNotNull(progress.science)
        assertEquals(0, progress.science.getQuantity(ScienceType.COMPASS).toLong())
        assertEquals(0, progress.science.getQuantity(ScienceType.WHEEL).toLong())
        assertEquals(1, progress.science.getQuantity(ScienceType.TABLET).toLong())
        assertEquals(0, progress.science.jokers.toLong())
    }

    @Test
    fun deserialize_joker() {
        val progress = gson!!.fromJson(JOKER_STR, ScienceProgress::class.java)
        assertNotNull(progress.science)
        assertEquals(0, progress.science.getQuantity(ScienceType.COMPASS).toLong())
        assertEquals(0, progress.science.getQuantity(ScienceType.WHEEL).toLong())
        assertEquals(0, progress.science.getQuantity(ScienceType.TABLET).toLong())
        assertEquals(1, progress.science.jokers.toLong())
    }

    companion object {

        private val COMPASS_STR = "\"COMPASS\""

        private val WHEEL_STR = "\"WHEEL\""

        private val TABLET_STR = "\"TABLET\""

        private val JOKER_STR = "\"any\""
    }
}

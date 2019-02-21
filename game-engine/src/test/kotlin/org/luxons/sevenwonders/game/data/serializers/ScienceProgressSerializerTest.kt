package org.luxons.sevenwonders.game.data.serializers

import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Before
import org.junit.Test
import org.luxons.sevenwonders.game.boards.ScienceType
import org.luxons.sevenwonders.game.effects.ScienceProgress
import org.luxons.sevenwonders.game.test.createScienceProgress
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

private const val TABLET_STR = "\"TABLET\""
private const val COMPASS_STR = "\"COMPASS\""
private const val WHEEL_STR = "\"WHEEL\""
private const val JOKER_STR = "\"any\""

class ScienceProgressSerializerTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = GsonBuilder().registerTypeAdapter(ScienceProgress::class.java, ScienceProgressSerializer()).create()
    }

    @Test
    fun serialize_emptyToNull() {
        val progress = createScienceProgress(0, 0, 0, 0)
        val json = gson.toJson(progress)
        assertEquals("null", json)
    }

    @Test
    fun serialize_oneCompass() {
        val progress = createScienceProgress(1, 0, 0, 0)
        val json = gson.toJson(progress)
        assertEquals(COMPASS_STR, json)
    }

    @Test
    fun serialize_oneWheel() {
        val progress = createScienceProgress(0, 1, 0, 0)
        val json = gson.toJson(progress)
        assertEquals(WHEEL_STR, json)
    }

    @Test
    fun serialize_oneTablet() {
        val progress = createScienceProgress(0, 0, 1, 0)
        val json = gson.toJson(progress)
        assertEquals(TABLET_STR, json)
    }

    @Test
    fun serialize_oneJoker() {
        val progress = createScienceProgress(0, 0, 0, 1)
        val json = gson.toJson(progress)
        assertEquals(JOKER_STR, json)
    }

    @Test
    fun serialize_failOnMultipleCompasses() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(2, 0, 0, 0)
            gson.toJson(progress)
        }
    }

    @Test
    fun serialize_failOnMultipleWheels() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(0, 2, 0, 0)
            gson.toJson(progress)
        }
    }

    @Test
    fun serialize_failOnMultipleTablets() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(0, 0, 2, 0)
            gson.toJson(progress)
        }
    }

    @Test
    fun serialize_failOnMultipleJokers() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(0, 0, 0, 2)
            gson.toJson(progress)
        }
    }

    @Test
    fun serialize_failOnMixedElements() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(1, 1, 0, 0)
            gson.toJson(progress)
        }
    }

    @Test
    fun deserialize_failOnEmptyString() {
        assertFailsWith<IllegalArgumentException> {
            gson.fromJson<ScienceProgress>("\"\"")
        }
    }

    @Test
    fun deserialize_failOnGarbageString() {
        assertFailsWith<IllegalArgumentException> {
            gson.fromJson<ScienceProgress>("thisisgarbage")
        }
    }

    @Test
    fun deserialize_compass() {
        val progress = gson.fromJson<ScienceProgress>(COMPASS_STR)
        assertNotNull(progress.science)
        assertEquals(1, progress.science.getQuantity(ScienceType.COMPASS))
        assertEquals(0, progress.science.getQuantity(ScienceType.WHEEL))
        assertEquals(0, progress.science.getQuantity(ScienceType.TABLET))
        assertEquals(0, progress.science.jokers)
    }

    @Test
    fun deserialize_wheel() {
        val progress = gson.fromJson<ScienceProgress>(WHEEL_STR)
        assertNotNull(progress.science)
        assertEquals(0, progress.science.getQuantity(ScienceType.COMPASS))
        assertEquals(1, progress.science.getQuantity(ScienceType.WHEEL))
        assertEquals(0, progress.science.getQuantity(ScienceType.TABLET))
        assertEquals(0, progress.science.jokers)
    }

    @Test
    fun deserialize_tablet() {
        val progress = gson.fromJson<ScienceProgress>(TABLET_STR)
        assertNotNull(progress.science)
        assertEquals(0, progress.science.getQuantity(ScienceType.COMPASS))
        assertEquals(0, progress.science.getQuantity(ScienceType.WHEEL))
        assertEquals(1, progress.science.getQuantity(ScienceType.TABLET))
        assertEquals(0, progress.science.jokers)
    }

    @Test
    fun deserialize_joker() {
        val progress = gson.fromJson<ScienceProgress>(JOKER_STR)
        assertNotNull(progress.science)
        assertEquals(0, progress.science.getQuantity(ScienceType.COMPASS))
        assertEquals(0, progress.science.getQuantity(ScienceType.WHEEL))
        assertEquals(0, progress.science.getQuantity(ScienceType.TABLET))
        assertEquals(1, progress.science.jokers)
    }
}

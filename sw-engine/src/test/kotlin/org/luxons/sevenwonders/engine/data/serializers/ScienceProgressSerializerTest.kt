package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test
import org.luxons.sevenwonders.engine.boards.ScienceType
import org.luxons.sevenwonders.engine.effects.ScienceProgress
import org.luxons.sevenwonders.engine.test.createScienceProgress
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

private const val TABLET_STR = "\"TABLET\""
private const val COMPASS_STR = "\"COMPASS\""
private const val WHEEL_STR = "\"WHEEL\""
private const val JOKER_STR = "\"any\""

class ScienceProgressSerializerTest {

    @Test
    fun serialize_emptyToNull() {
        val progress = createScienceProgress(0, 0, 0, 0)
        val json = Json.encodeToString(progress)
        assertEquals("null", json)
    }

    @Test
    fun serialize_oneCompass() {
        val progress = createScienceProgress(1, 0, 0, 0)
        val json = Json.encodeToString(progress)
        assertEquals(COMPASS_STR, json)
    }

    @Test
    fun serialize_oneWheel() {
        val progress = createScienceProgress(0, 1, 0, 0)
        val json = Json.encodeToString(progress)
        assertEquals(WHEEL_STR, json)
    }

    @Test
    fun serialize_oneTablet() {
        val progress = createScienceProgress(0, 0, 1, 0)
        val json = Json.encodeToString(progress)
        assertEquals(TABLET_STR, json)
    }

    @Test
    fun serialize_oneJoker() {
        val progress = createScienceProgress(0, 0, 0, 1)
        val json = Json.encodeToString(progress)
        assertEquals(JOKER_STR, json)
    }

    @Test
    fun serialize_failOnMultipleCompasses() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(2, 0, 0, 0)
            Json.encodeToString(progress)
        }
    }

    @Test
    fun serialize_failOnMultipleWheels() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(0, 2, 0, 0)
            Json.encodeToString(progress)
        }
    }

    @Test
    fun serialize_failOnMultipleTablets() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(0, 0, 2, 0)
            Json.encodeToString(progress)
        }
    }

    @Test
    fun serialize_failOnMultipleJokers() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(0, 0, 0, 2)
            Json.encodeToString(progress)
        }
    }

    @Test
    fun serialize_failOnMixedElements() {
        assertFailsWith<UnsupportedOperationException> {
            val progress = createScienceProgress(1, 1, 0, 0)
            Json.encodeToString(progress)
        }
    }

    @Test
    fun deserialize_failOnEmptyString() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString<ScienceProgress>("\"\"")
        }
    }

    @Test
    fun deserialize_failOnGarbageString() {
        assertFailsWith<IllegalArgumentException> {
            Json.decodeFromString<ScienceProgress>("thisisgarbage")
        }
    }

    @Test
    fun deserialize_compass() {
        val progress = Json.decodeFromString<ScienceProgress>(COMPASS_STR)
        assertNotNull(progress.science)
        assertEquals(1, progress.science.getQuantity(ScienceType.COMPASS))
        assertEquals(0, progress.science.getQuantity(ScienceType.WHEEL))
        assertEquals(0, progress.science.getQuantity(ScienceType.TABLET))
        assertEquals(0, progress.science.jokers)
    }

    @Test
    fun deserialize_wheel() {
        val progress = Json.decodeFromString<ScienceProgress>(WHEEL_STR)
        assertNotNull(progress.science)
        assertEquals(0, progress.science.getQuantity(ScienceType.COMPASS))
        assertEquals(1, progress.science.getQuantity(ScienceType.WHEEL))
        assertEquals(0, progress.science.getQuantity(ScienceType.TABLET))
        assertEquals(0, progress.science.jokers)
    }

    @Test
    fun deserialize_tablet() {
        val progress = Json.decodeFromString<ScienceProgress>(TABLET_STR)
        assertNotNull(progress.science)
        assertEquals(0, progress.science.getQuantity(ScienceType.COMPASS))
        assertEquals(0, progress.science.getQuantity(ScienceType.WHEEL))
        assertEquals(1, progress.science.getQuantity(ScienceType.TABLET))
        assertEquals(0, progress.science.jokers)
    }

    @Test
    fun deserialize_joker() {
        val progress = Json.decodeFromString<ScienceProgress>(JOKER_STR)
        assertNotNull(progress.science)
        assertEquals(0, progress.science.getQuantity(ScienceType.COMPASS))
        assertEquals(0, progress.science.getQuantity(ScienceType.WHEEL))
        assertEquals(0, progress.science.getQuantity(ScienceType.TABLET))
        assertEquals(1, progress.science.jokers)
    }
}

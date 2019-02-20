package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.luxons.sevenwonders.game.boards.Science
import org.luxons.sevenwonders.game.boards.ScienceType
import org.luxons.sevenwonders.game.effects.ScienceProgress
import java.lang.reflect.Type

internal class ScienceProgressSerializer : JsonSerializer<ScienceProgress>, JsonDeserializer<ScienceProgress> {

    override fun serialize(
        scienceProgress: ScienceProgress,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val science = scienceProgress.science

        if (science.size() > 1) {
            throw UnsupportedOperationException("Cannot serialize science containing more than one element")
        }

        for (type in ScienceType.values()) {
            val quantity = science.getQuantity(type)
            if (quantity == 1) {
                return context.serialize(type)
            }
        }

        return if (science.jokers == 1) JsonPrimitive("any") else JsonNull.INSTANCE
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ScienceProgress {
        val s = json.asString
        val science = Science()
        if ("any" == s) {
            science.addJoker(1)
        } else {
            science.add(deserializeScienceType(json, context), 1)
        }
        return ScienceProgress(science)
    }

    private fun deserializeScienceType(json: JsonElement, context: JsonDeserializationContext): ScienceType {
        return context.deserialize<ScienceType>(json, ScienceType::class.java)
                ?: throw IllegalArgumentException("Invalid science level " + json.asString)
    }
}

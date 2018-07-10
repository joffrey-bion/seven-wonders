package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.luxons.sevenwonders.game.resources.ResourceType
import org.luxons.sevenwonders.game.resources.Resources
import java.lang.reflect.Type

internal class ResourcesSerializer : JsonSerializer<Resources>, JsonDeserializer<Resources> {

    override fun serialize(resources: Resources, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val s = resources.asList().map { it.symbol }.joinToString("")
        return if (s.isEmpty()) JsonNull.INSTANCE else JsonPrimitive(s)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Resources {
        val s = json.asString
        val resources = Resources()
        for (c in s.toCharArray()) {
            resources.add(ResourceType.fromSymbol(c), 1)
        }
        return resources
    }
}

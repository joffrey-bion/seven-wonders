package org.luxons.sevenwonders.engine.data.serializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.luxons.sevenwonders.model.resources.ResourceType
import java.lang.reflect.Type

internal class ResourceTypesSerializer : JsonSerializer<List<ResourceType>>, JsonDeserializer<List<ResourceType>> {

    override fun serialize(
        resources: List<ResourceType>,
        typeOfSrc: Type,
        context: JsonSerializationContext,
    ): JsonElement {
        val s = resources.map { it.symbol }.joinToString("")
        return JsonPrimitive(s)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): List<ResourceType> = json.asString.map { ResourceType.fromSymbol(it) }
}

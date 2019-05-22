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

internal class ResourceTypeSerializer : JsonSerializer<ResourceType>, JsonDeserializer<ResourceType> {

    override fun serialize(type: ResourceType, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        JsonPrimitive(type.symbol)

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ResourceType {
        val str = json.asString
        if (str.isEmpty()) {
            throw IllegalArgumentException("Empty string is not a valid resource level")
        }
        return ResourceType.fromSymbol(str[0])
    }
}

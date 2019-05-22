package org.luxons.sevenwonders.engine.data.serializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.luxons.sevenwonders.engine.effects.ProductionIncrease
import org.luxons.sevenwonders.engine.resources.Production
import java.lang.reflect.Type

internal class ProductionIncreaseSerializer : JsonSerializer<ProductionIncrease>, JsonDeserializer<ProductionIncrease> {

    override fun serialize(
        productionIncrease: ProductionIncrease,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val production = productionIncrease.production
        val json = context.serialize(production)
        return if (!json.isJsonNull && !productionIncrease.isSellable) { JsonPrimitive("(${json.asString})") } else json
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ProductionIncrease {
        var prodJson = json

        var resourcesStr = prodJson.asString
        val isSellable = !resourcesStr.startsWith("(")
        if (!isSellable) {
            resourcesStr = unwrapBrackets(resourcesStr)
            prodJson = JsonPrimitive(resourcesStr)
        }
        val production = context.deserialize<Production>(prodJson, Production::class.java)
        return ProductionIncrease(production, isSellable)
    }

    private fun unwrapBrackets(str: String): String {
        return str.substring(1, str.length - 1)
    }
}

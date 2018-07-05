package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.luxons.sevenwonders.game.effects.ProductionIncrease
import org.luxons.sevenwonders.game.resources.Production
import java.lang.reflect.Type

class ProductionIncreaseSerializer : JsonSerializer<ProductionIncrease>, JsonDeserializer<ProductionIncrease> {

    override fun serialize(
        productionIncrease: ProductionIncrease,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val production = productionIncrease.production
        val json = context.serialize(production)
        return if (!json.isJsonNull && !productionIncrease.isSellable) { JsonPrimitive("(${json.asString})")} else json
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): ProductionIncrease {
        var json = json
        val productionIncrease = ProductionIncrease()

        var resourcesStr = json.asString
        val isSellable = !resourcesStr.startsWith("(")
        if (!isSellable) {
            resourcesStr = unwrapBrackets(resourcesStr)
            json = JsonPrimitive(resourcesStr)
        }
        productionIncrease.isSellable = isSellable

        val production = context.deserialize<Production>(json, Production::class.java)
        productionIncrease.production = production
        return productionIncrease
    }

    private fun unwrapBrackets(str: String): String {
        return str.substring(1, str.length - 1)
    }
}

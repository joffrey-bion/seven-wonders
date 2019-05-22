package org.luxons.sevenwonders.engine.data.serializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.engine.resources.Resources
import org.luxons.sevenwonders.model.resources.ResourceType
import java.lang.reflect.Type

internal class ProductionSerializer : JsonSerializer<Production>, JsonDeserializer<Production> {

    override fun serialize(production: Production, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val fixedResources = production.getFixedResources()
        val choices = production.getAlternativeResources()
        return when {
            fixedResources.isEmpty() -> serializeAsChoice(choices, context)
            choices.isEmpty() -> serializeAsResources(fixedResources, context)
            else -> throw IllegalArgumentException("Cannot serialize a production with mixed fixed resources and choices")
        }
    }

    private fun serializeAsChoice(choices: Set<Set<ResourceType>>, context: JsonSerializationContext): JsonElement {
        if (choices.isEmpty()) {
            return JsonNull.INSTANCE
        }
        if (choices.size > 1) {
            throw IllegalArgumentException("Cannot serialize a production with more than one choice")
        }
        val str = choices.flatMap { it }.map { it.symbol }.joinToString("/")
        return context.serialize(str)
    }

    private fun serializeAsResources(fixedResources: Resources, context: JsonSerializationContext): JsonElement {
        return context.serialize(fixedResources)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Production {
        val resourcesStr = json.asString
        val production = Production()
        if (resourcesStr.contains("/")) {
            production.addChoice(*createChoice(resourcesStr))
        } else {
            val fixedResources = context.deserialize<Resources>(json, Resources::class.java)
            production.addAll(fixedResources)
        }
        return production
    }

    private fun createChoice(choiceStr: String): Array<ResourceType> {
        return choiceStr.split("/").map { ResourceType.fromSymbol(it) }.toTypedArray()
    }
}

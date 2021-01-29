package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.luxons.sevenwonders.engine.resources.Production
import org.luxons.sevenwonders.model.resources.ResourceType

internal object ProductionSerializer : KSerializer<Production> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Production", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Production) {
        val fixedResources = value.getFixedResources()
        val choices = value.getAlternativeResources()
        when {
            fixedResources.isEmpty() -> choices.toChoiceString()?.let { encoder.encodeString(it) } ?: encoder.encodeNull()
            choices.isEmpty() -> encoder.encodeSerializableValue(ResourcesSerializer, fixedResources)
            else -> throw IllegalArgumentException("Cannot serialize a production with mixed fixed resources and choices")
        }
    }

    private fun List<Set<ResourceType>>.toChoiceString(): String? {
        if (isEmpty()) {
            return null
        }
        if (size > 1) {
            throw IllegalArgumentException("Cannot serialize a production with more than one choice")
        }
        return this[0].map { it.symbol }.joinToString("/")
    }

    override fun deserialize(decoder: Decoder): Production {
        val resourcesStr = decoder.decodeString()
        val production = Production()
        if (resourcesStr.contains("/")) {
            production.addChoice(*createChoice(resourcesStr))
        } else {
            production.addAll(ResourcesSerializer.deserializeString(resourcesStr))
        }
        return production
    }

    private fun createChoice(choiceStr: String): Array<ResourceType> {
        return choiceStr.split("/").map { ResourceType.fromSymbol(it) }.toTypedArray()
    }
}

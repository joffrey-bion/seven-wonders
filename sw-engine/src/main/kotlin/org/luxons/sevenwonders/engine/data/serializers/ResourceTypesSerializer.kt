package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.luxons.sevenwonders.model.resources.ResourceType

internal object ResourceTypesSerializer : KSerializer<List<ResourceType>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ResourceTypeList", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: List<ResourceType>) {
        encoder.encodeString(value.map { it.symbol }.joinToString(""))
    }

    override fun deserialize(decoder: Decoder): List<ResourceType> =
        decoder.decodeString().map { ResourceType.fromSymbol(it) }
}

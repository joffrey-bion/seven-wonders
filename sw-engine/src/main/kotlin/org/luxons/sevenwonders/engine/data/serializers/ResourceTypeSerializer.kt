package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.luxons.sevenwonders.model.resources.ResourceType

internal object ResourceTypeSerializer : KSerializer<ResourceType> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ResourceType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ResourceType) {
        encoder.encodeString(value.symbol.toString())
    }

    override fun deserialize(decoder: Decoder): ResourceType = ResourceType.fromSymbol(decoder.decodeString())
}

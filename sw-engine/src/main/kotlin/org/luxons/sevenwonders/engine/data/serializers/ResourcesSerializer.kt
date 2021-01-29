package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.luxons.sevenwonders.engine.resources.Resources
import org.luxons.sevenwonders.engine.resources.toResources
import org.luxons.sevenwonders.model.resources.ResourceType

internal object ResourcesSerializer : KSerializer<Resources> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Resources", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Resources) {
        val s = value.toList().map { it.symbol }.joinToString("")
        if (s.isEmpty()) encoder.encodeNull() else encoder.encodeString(s)
    }

    override fun deserialize(decoder: Decoder): Resources = deserializeString(decoder.decodeString())

    fun deserializeString(symbols: String): Resources = symbols.map { ResourceType.fromSymbol(it) to 1 }.toResources()
}

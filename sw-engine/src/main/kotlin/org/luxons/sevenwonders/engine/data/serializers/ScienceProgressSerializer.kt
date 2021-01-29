package org.luxons.sevenwonders.engine.data.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import org.luxons.sevenwonders.engine.boards.Science
import org.luxons.sevenwonders.engine.boards.ScienceType
import org.luxons.sevenwonders.engine.effects.ScienceProgress

internal object ScienceProgressSerializer : KSerializer<ScienceProgress> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("ScienceProgress", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: ScienceProgress) {
        if (value.science.size() > 1) {
            throw UnsupportedOperationException("Cannot serialize science containing more than one element")
        }
        if (value.science.jokers == 1) {
            encoder.encodeString("any")
            return
        }
        for (type in ScienceType.values()) {
            val quantity = value.science.getQuantity(type)
            if (quantity == 1) {
                encoder.encodeSerializableValue(serializer(), type)
                return
            }
        }
        encoder.encodeNull()
    }

    override fun deserialize(decoder: Decoder): ScienceProgress {
        val s = decoder.decodeString()
        val science = Science()
        if ("any" == s) {
            science.addJoker(1)
        } else {
            science.add(ScienceType.valueOf(s), 1)
        }
        return ScienceProgress(science)
    }
}

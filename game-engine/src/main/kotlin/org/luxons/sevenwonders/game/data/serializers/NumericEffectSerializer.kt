package org.luxons.sevenwonders.game.data.serializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import org.luxons.sevenwonders.game.effects.Effect
import org.luxons.sevenwonders.game.effects.GoldIncrease
import org.luxons.sevenwonders.game.effects.MilitaryReinforcements
import org.luxons.sevenwonders.game.effects.RawPointsIncrease
import java.lang.reflect.Type

internal class NumericEffectSerializer : JsonSerializer<Effect>, JsonDeserializer<Effect> {

    override fun serialize(effect: Effect, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val value: Int = when (effect) {
            is MilitaryReinforcements -> effect.count
            is GoldIncrease -> effect.amount
            is RawPointsIncrease -> effect.points
            else -> throw IllegalArgumentException("Unknown numeric effect " + effect.javaClass.name)
        }
        return JsonPrimitive(value)
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Effect {
        val value = json.asInt
        return when (typeOfT) {
            MilitaryReinforcements::class.java -> MilitaryReinforcements(value)
            GoldIncrease::class.java -> GoldIncrease(value)
            RawPointsIncrease::class.java -> RawPointsIncrease(value)
            else -> throw IllegalArgumentException("Unknown numeric effet " + typeOfT.typeName)
        }
    }
}

package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;

import org.luxons.sevenwonders.game.effects.Effect;
import org.luxons.sevenwonders.game.effects.GoldIncrease;
import org.luxons.sevenwonders.game.effects.MilitaryReinforcements;
import org.luxons.sevenwonders.game.effects.RawPointsIncrease;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class NumericEffectSerializer implements JsonSerializer<Effect>, JsonDeserializer<Effect> {

    @Override
    public JsonElement serialize(Effect effect, Type typeOfSrc, JsonSerializationContext context) {
        int value;
        if (MilitaryReinforcements.class.equals(typeOfSrc)) {
            value = ((MilitaryReinforcements) effect).getCount();
        } else if (GoldIncrease.class.equals(typeOfSrc)) {
            value = ((GoldIncrease) effect).getAmount();
        } else if (RawPointsIncrease.class.equals(typeOfSrc)) {
            value = ((RawPointsIncrease) effect).getPoints();
        } else {
            throw new IllegalArgumentException("Unknown numeric effect " + typeOfSrc.getTypeName());
        }
        return new JsonPrimitive(value);
    }

    @Override
    public Effect deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        int value = json.getAsInt();
        if (MilitaryReinforcements.class.equals(typeOfT)) {
            return new MilitaryReinforcements(value);
        } else if (GoldIncrease.class.equals(typeOfT)) {
            return new GoldIncrease(value);
        } else if (RawPointsIncrease.class.equals(typeOfT)) {
            return new RawPointsIncrease(value);
        }
        throw new IllegalArgumentException("Unknown numeric effet " + typeOfT.getTypeName());
    }
}

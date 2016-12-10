package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.luxons.sevenwonders.game.boards.Science;
import org.luxons.sevenwonders.game.boards.ScienceType;
import org.luxons.sevenwonders.game.effects.ScienceProgress;

public class ScienceProgressSerializer implements JsonSerializer<ScienceProgress>, JsonDeserializer<ScienceProgress> {

    @Override
    public JsonElement serialize(ScienceProgress scienceProgress, Type typeOfSrc, JsonSerializationContext context) {
        Science science = scienceProgress.getScience();

        if (science.size() > 1) {
            throw new UnsupportedOperationException("Cannot serialize science containing more than one element");
        }

        for (ScienceType type : ScienceType.values()) {
            int quantity = science.getQuantity(type);
            if (quantity == 1) {
                return context.serialize(type);
            }
        }

        if (science.getJokers() == 1) {
            return new JsonPrimitive("any");
        }

        return JsonNull.INSTANCE;
    }

    @Override
    public ScienceProgress deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
            JsonParseException {
        String s = json.getAsString();
        ScienceProgress scienceProgress = new ScienceProgress();
        Science science = new Science();
        if ("any".equals(s)) {
            science.addJoker(1);
        } else {
            science.add(context.deserialize(json, ScienceType.class), 1);
        }
        scienceProgress.setScience(science);
        return scienceProgress;
    }
}

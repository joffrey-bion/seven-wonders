package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.luxons.sevenwonders.game.resources.ResourceType;

public class ResourceTypeSerializer implements JsonSerializer<ResourceType>, JsonDeserializer<ResourceType> {

    @Override
    public JsonElement serialize(ResourceType type, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(type.getSymbol());
    }

    @Override
    public ResourceType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
            JsonParseException {
        String str = json.getAsString();
        if (str.isEmpty()) {
            throw new IllegalArgumentException("Empty string is not a valid resource type");
        }
        return ResourceType.fromSymbol(str.charAt(0));
    }
}

package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;

public class ResourcesSerializer implements JsonSerializer<Resources>, JsonDeserializer<Resources> {

    @Override
    public JsonElement serialize(Resources resources, Type typeOfSrc, JsonSerializationContext context) {
        String s = resources.getQuantities()
                            .entrySet()
                            .stream()
                            .flatMap(e -> Stream.generate(() -> e.getKey().getSymbol()).limit(e.getValue()))
                            .map(Object::toString)
                            .collect(Collectors.joining());
        return s.isEmpty() ? JsonNull.INSTANCE : new JsonPrimitive(s);
    }

    @Override
    public Resources deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
            JsonParseException {
        String s = json.getAsString();
        Resources resources = new Resources();
        for (char c : s.toCharArray()) {
            resources.add(ResourceType.fromSymbol(c), 1);
        }
        return resources;
    }
}

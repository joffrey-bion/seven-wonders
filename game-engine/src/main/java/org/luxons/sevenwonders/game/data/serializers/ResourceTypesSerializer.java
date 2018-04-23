package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.resources.ResourceType;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ResourceTypesSerializer implements JsonSerializer<List<ResourceType>>,
        JsonDeserializer<List<ResourceType>> {

    @Override
    public JsonElement serialize(List<ResourceType> resources, Type typeOfSrc, JsonSerializationContext context) {
        String s = resources.stream().map(ResourceType::getSymbol).map(Object::toString).collect(Collectors.joining());
        return new JsonPrimitive(s);
    }

    @Override
    public List<ResourceType> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String s = json.getAsString();
        List<ResourceType> resources = new ArrayList<>();
        for (char c : s.toCharArray()) {
            resources.add(ResourceType.fromSymbol(c));
        }
        return resources;
    }
}

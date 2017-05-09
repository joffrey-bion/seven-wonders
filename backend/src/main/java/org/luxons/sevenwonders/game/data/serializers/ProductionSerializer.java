package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;

public class ProductionSerializer implements JsonSerializer<Production>, JsonDeserializer<Production> {

    @Override
    public JsonElement serialize(Production production, Type typeOfSrc, JsonSerializationContext context) {
        Resources fixedResources = production.getFixedResources();
        Set<Set<ResourceType>> choices = production.getAlternativeResources();
        if (fixedResources.isEmpty()) {
            return serializeAsChoice(choices, context);
        } else if (choices.isEmpty()) {
            return serializeAsResources(fixedResources, context);
        } else {
            throw new IllegalArgumentException("Cannot serialize a production with mixed fixed resources and choices");
        }
    }

    private static JsonElement serializeAsChoice(Set<Set<ResourceType>> choices, JsonSerializationContext context) {
        if (choices.isEmpty()) {
            return JsonNull.INSTANCE;
        }
        if (choices.size() > 1) {
            throw new IllegalArgumentException("Cannot serialize a production with more than one choice");
        }
        String str = choices.stream()
                            .flatMap(Set::stream)
                            .map(ResourceType::getSymbol)
                            .map(Object::toString)
                            .collect(Collectors.joining("/"));
        return context.serialize(str);
    }

    private static JsonElement serializeAsResources(Resources fixedResources, JsonSerializationContext context) {
        return context.serialize(fixedResources);
    }

    @Override
    public Production deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String resourcesStr = json.getAsString();
        Production production = new Production();
        if (resourcesStr.contains("/")) {
            production.addChoice(createChoice(resourcesStr));
        } else {
            Resources fixedResources = context.deserialize(json, Resources.class);
            production.addAll(fixedResources);
        }
        return production;
    }

    private ResourceType[] createChoice(String choiceStr) {
        String[] symbols = choiceStr.split("/");
        ResourceType[] choice = new ResourceType[symbols.length];
        for (int i = 0; i < symbols.length; i++) {
            if (symbols[i].length() != 1) {
                throw new IllegalArgumentException("Choice elements must be resource types, got " + symbols[i]);
            }
            choice[i] = ResourceType.fromSymbol(symbols[i].charAt(0));
        }
        return choice;
    }
}

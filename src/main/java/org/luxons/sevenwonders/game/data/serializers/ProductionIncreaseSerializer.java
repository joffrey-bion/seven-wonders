package org.luxons.sevenwonders.game.data.serializers;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.luxons.sevenwonders.game.effects.ProductionIncrease;
import org.luxons.sevenwonders.game.resources.Production;
import org.luxons.sevenwonders.game.resources.ResourceType;
import org.luxons.sevenwonders.game.resources.Resources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ProductionIncreaseSerializer implements JsonSerializer<ProductionIncrease>,
        JsonDeserializer<ProductionIncrease> {

    @Override
    public JsonElement serialize(ProductionIncrease productionIncrease, Type typeOfSrc,
                                 JsonSerializationContext context) {
        Production production = productionIncrease.getProduction();
        Resources fixedResources = production.getFixedResources();
        List<Set<ResourceType>> choices = production.getAlternativeResources();
        if (fixedResources.isEmpty()) {
            return serializeAsChoice(choices, context);
        } else if (choices.isEmpty()) {
            return serializeAsResources(fixedResources, context);
        } else {
            throw new IllegalArgumentException("Cannot serialize a production with mixed fixed resources and choices");
        }
    }

    private JsonElement serializeAsResources(Resources fixedResources, JsonSerializationContext context) {
        return context.serialize(fixedResources);
    }

    private JsonElement serializeAsChoice(List<Set<ResourceType>> choices, JsonSerializationContext context) {
        if (choices.isEmpty()) {
            return JsonNull.INSTANCE;
        }
        if (choices.size() > 1) {
            throw new IllegalArgumentException("Cannot serialize a production with more than one choice");
        }
        String str = choices.get(0).stream()
                            .map(ResourceType::getSymbol)
                            .map(Object::toString)
                            .collect(Collectors.joining("/"));
        return new JsonPrimitive(str);
    }

    @Override
    public ProductionIncrease deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
            JsonParseException {
        String s = json.getAsString();
        ProductionIncrease productionIncrease = new ProductionIncrease();
        Production production = new Production();
        if (s.contains("/")) {
            production.addChoice(createChoice(s));
        } else {
            Resources fixedResources = context.deserialize(json, Resources.class);
            production.addAll(fixedResources);
        }
        productionIncrease.setProduction(production);
        return productionIncrease;
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
